package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PayConfigUpdateReq {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "pid")
    private Integer pid;

    @ApiModelProperty(value = "secretKey")
    private String secretKey;

    @ApiModelProperty(value = "回调地址")
    private String notifyUrl;

    @ApiModelProperty(value = "页面跳转地址")
    private String returnUrl;

    @ApiModelProperty(value = "支付请求地址")
    private String submitUrl;

    @ApiModelProperty(value = "支付宝appid")
    private String aliAppId;

    @ApiModelProperty(value = "支付宝应用私钥")
    private String aliPrivateKey;

    @ApiModelProperty(value = "支付宝应用公钥")
    private String aliPublicKey;

    @ApiModelProperty(value = "支付宝接口地址")
    private String aliGatewayUrl;

    @ApiModelProperty(value = "支付宝回调地址")
    private String aliNotifyUrl;

    @ApiModelProperty(value = "支付宝页面跳转地址")
    private String aliReturnUrl;

    @ApiModelProperty(value = "支付类型 0 易支付 1微信 2支付宝 3支付宝、微信")
    private Integer payType;
}
