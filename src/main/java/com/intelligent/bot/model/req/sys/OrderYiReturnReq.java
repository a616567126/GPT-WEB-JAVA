package com.intelligent.bot.model.req.sys;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class OrderYiReturnReq {


    /**
     * 订单id
     */
    @NotNull
    private Long orderId;

}
