package com.lsz.seckill.controller;


import com.lsz.seckill.pojo.User;
import com.lsz.seckill.service.IOrderService;
import com.lsz.seckill.vo.OrderDetail;
import com.lsz.seckill.vo.RespBean;
import com.lsz.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lsz
 * @since 2021-08-21
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    @ResponseBody
    public RespBean detail(User user,Long orderId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetail orderDetail = orderService.detail(orderId);
        return RespBean.success(orderDetail);

    }

}
