package com.intelligent.bot.model.req.sys;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TempUserAuthReq {

    /**
     * 用户浏览器指纹
     */
    @NotNull
    private String browserFingerprint;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 授权token
     */
    @NotNull
    private String authToken;
}
