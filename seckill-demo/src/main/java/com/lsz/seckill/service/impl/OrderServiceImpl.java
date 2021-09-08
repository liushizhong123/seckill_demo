package com.lsz.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsz.seckill.execption.GlobalException;
import com.lsz.seckill.mapper.OrderMapper;
import com.lsz.seckill.mapper.ScekillOrderMapper;
import com.lsz.seckill.pojo.*;
import com.lsz.seckill.service.IGoodsService;
import com.lsz.seckill.service.IOrderService;
import com.lsz.seckill.service.IScekillOrderService;
import com.lsz.seckill.service.ISeckillGoodsService;
import com.lsz.seckill.util.MD5Util;
import com.lsz.seckill.util.UUIDUtil;
import com.lsz.seckill.vo.GoodsVo;
import com.lsz.seckill.vo.OrderDetail;
import com.lsz.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lsz
 * @since 2021-08-21
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IScekillOrderService scekillOrderService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        /**
         * 秒杀商品表，减去库存
         */
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillGoods seckillGoods =  seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id",goods.getId()));
        //秒杀商品库存减一
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        //更新秒杀商品库存
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql(
                "stock_count = stock_count-1").eq(
                        "goods_id", goods.getId()).gt("stock_count", 0));
        if(!result){
            return null;
        }
        if(seckillGoods.getStockCount() < 1){
            //判断是否还有库存
            valueOperations.set("isStockEmpty:" + goods.getId(),"0");
            return null;
        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        ScekillOrder scekillOrder = new ScekillOrder();
        scekillOrder.setUserId(user.getId());
        scekillOrder.setOrderId(order.getId());
        scekillOrder.setGoodsId(goods.getId());
        scekillOrderService.save(scekillOrder);
        //存入redis
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(),scekillOrder);
        //返回订单
        return order;
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetail detail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        //获取订单
        Order order = orderMapper.selectById(orderId);
        //获取商品
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setGoodsVo(goodsVo);
        return orderDetail;
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */

    @Override
    public String createPath(User user, Long goodsId) {
        //生成接口地址
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId,str,60, TimeUnit.SECONDS);
        return  str;
    }


    /**
     * 验证秒杀地址
     * @return
     */

    @Override
    public boolean checkPath(User user,Long goodsId,String path) {
        if(user == null || goodsId < 0 || StringUtils.isEmpty(path)){
            return false;
        }
        //得到redis中应该有的path
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    /**
     *验证验证码的正确性
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(user == null || goodsId < 0 || StringUtils.isEmpty(captcha)){
            return false;
        }
        String redisCaptcha  = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
