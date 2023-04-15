package com.chat.java.model.res;

import com.chat.java.model.Order;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户表(User)实体类
 *
 * @author  
 * @since 2022-03-12 14:33:27
 */
@Data
public class OrderUserRes extends Order implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    @ApiModelProperty(value = "订单id", position = 5)
    private Long id;
    @ApiModelProperty(value = "商品名", position = 5)
    private String productName;


    @ApiModelProperty(value = "金额", position = 8)
    private BigDecimal price;

    @ApiModelProperty(value = "状态 0待支付 1支付完成", position = 9)
    private Integer state;


    @ApiModelProperty(value = "支付方式 wxpay、alipay、qqpay", position = 10)
    private String payType;


    @ApiModelProperty(value = "购买数量", position = 11)
    private Integer payNumber;


    @ApiModelProperty(value = "支付完成时间", position = 3,hidden=true)
    private LocalDateTime operateTime;

    @ApiModelProperty(value = "下单时间", position = 2,hidden=true)
    private LocalDateTime createTime;


}
