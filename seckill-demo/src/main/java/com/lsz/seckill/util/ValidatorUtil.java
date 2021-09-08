package com.lsz.seckill.util;


import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    private static String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";

    private static final Pattern mobile_pattern = Pattern.compile(regex);


    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return false;
        }else if(mobile.length() != 11){
            return false;
        }

        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }
}