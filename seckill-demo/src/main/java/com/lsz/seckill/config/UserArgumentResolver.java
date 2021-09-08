package com.lsz.seckill.config;


import com.lsz.seckill.pojo.User;
import com.lsz.seckill.service.IUserService;
import com.lsz.seckill.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private  IUserService iUserService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //得到参数的类型，如果是符合的返回true ,进行处理
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        //创建cookie
        assert request != null;
        String ticket = CookieUtil.getCookie(request,"userTicket");
        // cookie 为空
        if(StringUtils.isEmpty(ticket)){
            return null;
        }
        //根据 cookie 从 redis 中得到 user
        return iUserService.getUserByCookie(ticket,response) ;
    }
}
