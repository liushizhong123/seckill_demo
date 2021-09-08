package com.lsz.seckill.vo;


import com.lsz.seckill.validator.IsMobile;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

//用户登入模型
@Data
public class LoginVo {

    @NotNull
    @IsMobile(required = true)
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
