package com.lsz.seckill.execption;


import com.lsz.seckill.vo.RespBean;
import com.lsz.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e){
        //判断是否是全局异常
        if(e instanceof GlobalException){
            GlobalException ex =(GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());

         //判断是否为绑定异常
        }else if(e instanceof BindException){
            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            respBean.setMessage("参数校验异常：" + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }

        return RespBean.error(RespBeanEnum.ERROR);
    }
}
