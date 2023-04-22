package com.chat.java.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("pay_config")
public class PayConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

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

    @ApiModelProperty(value = "微信支付的appid")
    private String wxAppid;

    @ApiModelProperty(value = "微信支付直连商户号")
    private String wxMchid;

    @ApiModelProperty(value = "微信NativePostApi地址")
    private String wxNativeUrl;

    @ApiModelProperty(value = "微信native回调地址")
    private String wxNativeReturnUrl;

    @ApiModelProperty(value = "微信apiV3秘钥")
    private String wxV3Secret;

    @ApiModelProperty(value = "商户api序列号")
    private String wxSerialNo;

    @ApiModelProperty(value = "商户证书内容apiclient_key.pem")
    private String wxPrivateKey;

}
