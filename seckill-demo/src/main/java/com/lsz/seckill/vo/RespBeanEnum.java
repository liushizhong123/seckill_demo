package com.lsz.seckill.vo;


import lombok.*;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务器异常"),

    //登入模块5002xxx
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    MOBILE_ERROR(500211,"手机号码不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    SESSION_ERROR(500213,"用户不存在"),

    //秒杀模块 5005xxx
    EMPTY_STOCK(500510,"库存不足"),
    REPEATE_ERROR(500511,"一人限购一件"),
    REQUEST_ILLEGAL(500512,"请求信息不合法"),
    CAPTCHA_ERROR(500513,"验证码错误,请重新输入"),
    ACCESS_LIMIT_REAHCED(500514,"访问过于频繁，请稍后再试"),

    //订单模块 5003xx
    ORDER_NOT_EXIST(500310,"订单信息不存在");




    private final long code;
    private final String message;
}
