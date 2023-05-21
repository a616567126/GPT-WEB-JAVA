package com.intelligent.bot.model.req.sys;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientUpdatePasswordReq {

    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String password;
}
