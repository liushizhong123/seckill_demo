package com.lsz.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsz.seckill.execption.GlobalException;
import com.lsz.seckill.mapper.UserMapper;
import com.lsz.seckill.pojo.User;
import com.lsz.seckill.service.IUserService;
import com.lsz.seckill.util.CookieUtil;
import com.lsz.seckill.util.MD5Util;
import com.lsz.seckill.util.UUIDUtil;
import com.lsz.seckill.util.ValidatorUtil;
import com.lsz.seckill.vo.LoginVo;
import com.lsz.seckill.vo.RespBean;
import com.lsz.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lsz
 * @since 2021-08-19
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {

        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

//        //判空处理
//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
//            return  RespBean.error(RespBeanEnum.LOGIN_ERROR);
//
//        }
//
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }


        User user = userMapper.selectById(mobile);
        if(null == user){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //判断密码是否正确
        if(!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
           throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成 cookie （值）
        String ticket = UUIDUtil.uuid();
        //为用户绑定 cookie
//        request.getSession().setAttribute(ticket,user);
        //将用户信息存入redis中 （ticket 为cookie的值）
        redisTemplate.opsForValue().set("user:" + ticket,user);
        //设置 cookie （userTick 是 cookieName）
        CookieUtil.setCookie(response,"userTicket",ticket);
        //用户验证通过
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket,HttpServletResponse response) {
        //用户没有cookie
        if(StringUtils.isEmpty(userTicket)){
            return null;
        }
        //根据cookie得到用户
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        //用户存在，再次设置cookie
        if(user !=  null){
            CookieUtil.setCookie(response,"userTicket",userTicket);
        }
        return user;
    }
}
