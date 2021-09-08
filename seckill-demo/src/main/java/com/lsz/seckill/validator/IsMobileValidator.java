package com.lsz.seckill.validator;

import com.lsz.seckill.util.ValidatorUtil;
import com.lsz.seckill.validator.IsMobile;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private  boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
      boolean required =  constraintAnnotation.required();
    }

    @Override
    /**
     * s为接收到的参数
     */
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
            if(required){
                ValidatorUtil.isMobile(s);
            }else {
                if(StringUtils.isEmpty(s)){
                    return true;
                }else {
                    return ValidatorUtil.isMobile(s);
                }
            }
        return false;
    }
}
