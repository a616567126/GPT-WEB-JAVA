package com.intelligent.bot.model.req.sys;

import lombok.Data;
import lombok.NonNull;


@Data
public class EmailRegisterReq {

    /**
     * 姓名
     */
    @NonNull
    private String name;

    /**
     * 手机号
     */
    @NonNull
    private String mobile;


    /**
     * 邮件验证码
     */
    @NonNull
    private String emailCode;


    /**
     * 邮件地址
     */
    @NonNull
    private String email;

    /**
     * 浏览器指纹
     */
    private String browserFingerprint;
}
