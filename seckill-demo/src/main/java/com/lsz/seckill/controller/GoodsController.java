package com.lsz.seckill.controller;


import com.lsz.seckill.pojo.User;
import com.lsz.seckill.service.IGoodsService;
import com.lsz.seckill.service.IUserService;
import com.lsz.seckill.service.impl.GoodsServiceImpl;
import com.lsz.seckill.vo.GoodsDetail;
import com.lsz.seckill.vo.GoodsVo;
import com.lsz.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.WebConnection;
import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    /**
     * 跳转到商品列表列进行秒杀
     * @param session
     * @param model
     * @param ticket
     * @return goodsList 页面
     */

    @Autowired
    private  IUserService iUserService;

    @Autowired
    private IGoodsService iGoodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private  ThymeleafViewResolver thymeleafViewResolver;
    /**
     * 功能描述：跳转商品列表
     * windos :qps ： 576.7
     * linux: qps: 7.6
     * @param model
     * @param user
     * @return
     */

    /**
     * 不是单纯的跳转页面，而是返回一个对象,将页面缓存起来，放在redis中
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model,User user,
                         HttpServletRequest request, HttpServletResponse response){
        //redis 中获取页面，如果页面不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //得到页面
        String html = (String) valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user",user);
        model.addAttribute("goodsList",iGoodsService.findGoodsVo());
        //页面为空，手动渲染页面,存入redis中并且返回
        WebContext webContext = new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        //页面不为空,存入redis并设置过期时间一分钟
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html, 60,TimeUnit.SECONDS);
        }
        return html;
    }

    @RequestMapping(value = "/toDetail2/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model, User user, @PathVariable long goodsId, HttpServletRequest request,HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user",user);
        GoodsVo goodsVo = iGoodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态
        int secKillStatus = 0;
        // 秒杀倒计时
        int remainSeconds = 0;
        // 当前时间在秒杀开始时间之前
        if(nowDate.before(startDate)){
            //秒杀还未开始
            secKillStatus = 0;
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){
            //秒杀已结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("goods",iGoodsService.findGoodsVoByGoodsId(goodsId));
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:" + goodsId,html,60,TimeUnit.SECONDS);
        }
        return html;
    }


    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable long goodsId){
        GoodsVo goodsVo = iGoodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态
        int secKillStatus = 0;
        // 秒杀倒计时
        int remainSeconds = 0;
        // 当前时间在秒杀开始时间之前
        if(nowDate.before(startDate)){
            //秒杀还未开始
            secKillStatus = 0;
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){
            //秒杀已结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetail goodsDetail = new GoodsDetail();
        goodsDetail.setUser(user);
        goodsDetail.setGoodsVo(goodsVo);
        goodsDetail.setRemainSeconds(remainSeconds);
        goodsDetail.setSecKillStatus(secKillStatus);
        //返回对象
        return RespBean.success(goodsDetail);
    }
}
