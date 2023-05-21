package com.intelligent.bot.model.req.sys;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientHomeReq {

    /**
     * 对话类型
     */
    @NotNull(message = "对话类型不能为空")
    private Integer sendType;
}
