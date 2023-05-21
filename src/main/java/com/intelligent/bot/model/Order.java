package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_order")
public class Order extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;


    /**
     * 商品id
     */
    private Long productId;

    /**
     * 用户id
     */
    private Long userId;

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
     * 支付消息
     */
    private String msg;


}
