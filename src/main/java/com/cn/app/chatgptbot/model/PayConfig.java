package com.cn.app.chatgptbot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.app.chatgptbot.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户表(User)实体类
 *
 * @author  
 * @since 2022-03-12 14:33:27
 */
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


}
