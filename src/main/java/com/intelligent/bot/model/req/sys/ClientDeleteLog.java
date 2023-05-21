package com.intelligent.bot.model.req.sys;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientDeleteLog {

    /**
     * 日志id
     */
    @NotNull
    private Long id;
}
