package com.cn.app.chatgptbot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.app.chatgptbot.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户表(User)实体类
 *
 * @author  
 * @since 2022-03-12 14:33:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_order")
public class Order extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 商品id
     */
    @ApiModelProperty(value = "商品id", position = 5)
    private Long productId;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", position = 7)
    private Long userId;
    /**
     * 金额
     */
    @ApiModelProperty(value = "金额", position = 8)
    private BigDecimal price;
    /**
     * 状态 0待支付 1支付完成
     */
    @ApiModelProperty(value = "状态 0待支付 1支付完成", position = 9)
    private Integer state;

    /**
     * 支付方式 wxpay、alipay、qqpay
     */
    @ApiModelProperty(value = "支付方式 wxpay、alipay、qqpay", position = 10)
    private String payType;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量", position = 11)
    private Integer payNumber;

    @ApiModelProperty(value = "支付结果消息", position = 11)
    private String msg;

    @ApiModelProperty(value = "平台订单号", position = 11)
    private String tradeNo;


}
