package com.chat.java.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * ClassName:CreateOrderReq
 * Package:com.chat.java.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/22 - 09:03
 * @Version: v1.0
 */
@Data
public class CreateOrderRes {

    @ApiModelProperty(value = "订单id")
    private String outTradeNo;

    @ApiModelProperty(value = "商户id")
    private Integer pid;

    @ApiModelProperty(value = "商户密钥")
    private String key;

    @ApiModelProperty(value = "商品名")
    private String name;

    @ApiModelProperty(value = "价格")
    private String money;

    @ApiModelProperty(value = "加密方式")
    private String signType = "MD5";

    @ApiModelProperty(value = "支付方式")
    private String type ;

    @ApiModelProperty(value = "签名字符串")
    private String sign ;

    @ApiModelProperty(value = "请求地址")
    private String url ;

    @ApiModelProperty(value = "异步通知地址")
    private String notifyUrl ;

    @ApiModelProperty(value = "跳转通知地址")
    private String returnUrl ;


}
