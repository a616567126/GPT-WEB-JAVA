package com.intelligent.bot.model.res.sys;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class ReturnUrlRes implements Serializable {


    /**
     * 返回状态码 1为成功，其它值为失败
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

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
     * 商户ID
     */
    private Integer pid;

    /**
     * 创建订单时间
     */
    private String addtime;

    /**
     * 完成交易时间
     */
    private String endtime;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品金额
     */
    private BigDecimal money;

    /**
     * 支付状态
     */
    private Integer status;

    /**
     * 业务扩展参数
     */
    private String param;

    /**
     * 支付者账号
     */
    private String buyer;

}
