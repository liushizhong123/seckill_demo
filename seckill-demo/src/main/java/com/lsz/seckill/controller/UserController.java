package com.lsz.seckill.controller;


import com.lsz.seckill.pojo.User;
import com.lsz.seckill.rabbitmq.MQsSender;
import com.lsz.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lsz
 * @since 2021-08-19
 */
@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    private MQsSender mQsSender;
    /**
     * 功能测试：用户信息（测试）
     * @param user
     * @return
     */

    @ResponseBody
    @RequestMapping("/info")
    public RespBean info(User user){
        return RespBean.success(user);
    }

//    /**
//     * 测试发送rabbitmq消息
//     */
//    @ResponseBody
//    @RequestMapping("/mq")
//    public void mq(){
//        mQsSender.send("hello");
//    }
//
//    /**
//     * fanout模式测试
//     */
//
//    @ResponseBody
//    @RequestMapping("/mqfanout")
//    public void mq01(){
//        mQsSender.send("hello");
//    }
}
