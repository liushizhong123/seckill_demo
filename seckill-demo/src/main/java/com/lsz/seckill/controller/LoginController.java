package com.lsz.seckill.controller;


import com.lsz.seckill.service.IUserService;
import com.lsz.seckill.vo.LoginVo;
import com.lsz.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("login")
@Slf4j  //打印日志
public class LoginController {


    @Autowired
    private IUserService IUserService;


    @RequestMapping("toLogin")
    public String toLogin(){

        return "login";
    }


    /**
     * 登入功能实现
     * @param loginVo
     * @return RespBean
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        return IUserService.doLogin(loginVo,request,response);
    }

}
