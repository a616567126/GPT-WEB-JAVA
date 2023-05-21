package com.intelligent.bot.model.req.sys;

import lombok.Data;
import lombok.NonNull;


@Data
public class CreateYiOrderReq {


    /**
     * 产品id
     */
    @NonNull
    private Long productId;

    /**
     * 数量
     */
    @NonNull
    private Integer payNumber;

    /**
     * 支付类型
     */
    @NonNull
    private String payType;
}
