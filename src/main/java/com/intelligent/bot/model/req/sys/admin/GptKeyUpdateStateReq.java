package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class GptKeyUpdateStateReq {

    /**
     * id
     */
    @NotNull
    private Long id;

    /**
     * 状态 0 启用 1禁用
     */
    @NotNull
    private Integer state;
}
