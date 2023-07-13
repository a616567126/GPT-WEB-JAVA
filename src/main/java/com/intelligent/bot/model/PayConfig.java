package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("pay_config")
public class PayConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 易支付pid
     */
    private Integer pid;

    /**
     * 易支付secretKey
     */
    private String secretKey;

    /**
     * 易支付支付请求地址
     */
    private String submitUrl;

    /**
     * 易支付订单查询api
     */
    private String apiUrl;

    /**
     * 支付类型 0 易支付 1卡密
     */
    private Integer payType;

    /**
     * 微信支付的appid
     */
    private String wxAppid;

    /**
     * 微信支付直连商户号
     */
    private String wxMchid;

    /**
     * 微信apiV3秘钥
     */
    private String wxV3Secret;

    /**
     * 商户api序列号
     */
    private String wxSerialNo;

    /**
     * 商户证书内容apiclient_key.pem
     */
    private String wxPrivateKey;


}
