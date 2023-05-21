package com.intelligent.bot.model.res.sys;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClientOrderRes {

    /**
     * 订单id
     */
    private Long id;

    /**
     * 订单金额
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer payNumber;

    /**
     * 状态 0待支付 1支付完成 2支付失败
     */
    private Integer state;

    /**
     * 支付方式 wxpay、alipay、qqpay
     */
    private String payType;

    /**
     * 平台订单号、卡密
     */
    private String tradeNo;

    /**
     * 商品名
     */
    private String productName;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;

}
