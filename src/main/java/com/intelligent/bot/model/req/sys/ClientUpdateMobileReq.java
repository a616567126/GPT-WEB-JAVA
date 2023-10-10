package com.intelligent.bot.model.req.sys;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientUpdateMobileReq {

    /**
     * 手机号
     */
    @NotNull(message = "手机号不能为空")
    private String mobile;
}
