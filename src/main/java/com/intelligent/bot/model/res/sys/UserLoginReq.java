package com.intelligent.bot.model.res.sys;

import lombok.Data;

import javax.validation.constraints.NotEmpty;



@Data
public class UserLoginReq {

    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    private String password;

    /**
     * 是否手机端登录默认客户端登录
     */
    private Integer isMobile = 0;
}
