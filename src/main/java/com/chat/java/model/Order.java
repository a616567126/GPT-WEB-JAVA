package com.chat.java.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_order")
public class Order extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;


    @ApiModelProperty(value = "商品id", position = 5)
    private Long productId;

    @ApiModelProperty(value = "用户id", position = 7)
    private Long userId;

    @ApiModelProperty(value = "金额", position = 8)
    private BigDecimal price;

    @ApiModelProperty(value = "状态 0待支付 1支付完成", position = 9)
    private Integer state;

    @ApiModelProperty(value = "支付方式 wxpay、alipay、qqpay", position = 10)
    private String payType;

    @ApiModelProperty(value = "购买数量", position = 11)
    private Integer payNumber;

    @ApiModelProperty(value = "支付结果消息", position = 11)
    private String msg;

    @ApiModelProperty(value = "平台订单号", position = 11)
    private String tradeNo;


}
