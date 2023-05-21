package com.intelligent.bot.model.req.sys;

import lombok.Data;


@Data
public class OrderYiCallBackReq {


    /**
     * 商户id
     */
    private Integer pid;

    /**
     * 易支付订单号
     */
    private String trade_no;

    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 支付方式
     */
    private String type;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品金额
     */
    private String money;

    /**
     * 支付状态只有TRADE_SUCCESS是成功
     */
    private String trade_status;

    /**
     * 业务扩展参数
     */
    private String param;

    /**
     * 签名字符串
     */
    private String sign;

    /**
     * 签名类型
     */
    private String sign_type;

}
