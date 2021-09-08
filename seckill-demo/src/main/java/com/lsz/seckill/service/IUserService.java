package com.lsz.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsz.seckill.pojo.User;
import com.lsz.seckill.vo.LoginVo;
import com.lsz.seckill.vo.RespBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lsz
 * @since 2021-08-19
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    // 根据用户 cookie 获取在redis中的用户信息 （userTicket 是cookie的值）
    User getUserByCookie(String userTicket,HttpServletResponse response);
}
