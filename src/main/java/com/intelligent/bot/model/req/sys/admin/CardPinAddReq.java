package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;

@Data
public class CardPinAddReq {

    /**
     * 次数
     */
    private Integer number;

    /**
     * 生成批次
     */
    private Integer batch;



}
