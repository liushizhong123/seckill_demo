package com.lsz.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lsz.seckill.execption.GlobalException;
import com.lsz.seckill.pojo.*;
import com.lsz.seckill.rabbitmq.MQsSender;
import com.lsz.seckill.service.IGoodsService;
import com.lsz.seckill.service.IOrderService;
import com.lsz.seckill.service.IScekillOrderService;
import com.lsz.seckill.vo.GoodsVo;
import com.lsz.seckill.vo.RespBean;
import com.lsz.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService iGoodsService;
    @Autowired
    private IScekillOrderService scekillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQsSender mQsSender;
    @Autowired
    private RedisScript redisScript;

    private Map<Long,Boolean> emptyStockMap = new HashMap<>();


    /**
     * 商品秒杀 windos: gps : 113.9
     *                       678.8
     *                       901.3
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, User user,long goodsId){
        if(user == null) {
            return "login";
        }
        model.addAttribute("user",user);
        GoodsVo goods = iGoodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return  "secKillFail";
        }
        //查询订单表，查看用户是否已经抢过商品
        ScekillOrder scekillOrder = scekillOrderService.getOne(new QueryWrapper<ScekillOrder>().eq(
                "user_id",
                user.getId()).eq(
                        "goods_id",
                goodsId));
        if(scekillOrder != null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user,goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return "orderDetail";
    }

    @RequestMapping(value = "/{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, long goodsId){
        if(user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user,goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //判断是否重复抢购
        ScekillOrder scekillOrder = (ScekillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(scekillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //空库存，直接返回，不再访问redis
        if(emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //秒杀商品库存预减
        //返回库存数量
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        //Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        if(stock < 0){
            emptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //得到消息对象
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        //发送消息
        mQsSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));
        return RespBean.success(0);

    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId：成功；-1：秒杀失败；0：排队中。
     */
    @RequestMapping(value = "/getResult",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = scekillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if(null == user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //限制访问次数，5秒5次
        String uri = request.getRequestURI();
        //默认验证码
        captcha = "0";
        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
        if(count == null){
            //第一次访问
            valueOperations.set(uri + ":" + user.getId(),1,5,TimeUnit.SECONDS);
        }else if(count < 5){
            valueOperations.increment(uri + ":" + user.getId());
        }else {
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REAHCED);
        }
        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if(!check){
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);

    }

    /**
     * 生成验证码，存入redis，输出验证码
     * @param user
     * @param goodsId
     * @param response
     */

   @RequestMapping(value = "/captcha" ,method = RequestMethod.GET)
   public  void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if(user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam","No-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        //生成验证码，放入redis中
       ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
       redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId,captcha.text(),300, TimeUnit.SECONDS);
       //验证码输出流直接输出
       try {
           captcha.out(response.getOutputStream());
       } catch (IOException e) {
           log.info("验证码生成失败：" + e.getMessage());
       }
   }

    /**
     * 系统初始化：将秒杀商品库存加载进redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = iGoodsService.findGoodsVo();
        //判断库存是否为空
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        //将库存存入 redis
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:"+ goodsVo.getId(),goodsVo.getStockCount());
            //设置redis库存标记
            emptyStockMap.put(goodsVo.getId(),false);
        }
    );
    }
}
