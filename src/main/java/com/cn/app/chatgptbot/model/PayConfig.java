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
    /**
     * pid
     */
    @ApiModelProperty(value = "pid", position = 5)
    private Integer pid;
    /**
     * secretKey
     */
    @ApiModelProperty(value = "secretKey", position = 7)
    private String secretKey;

    @ApiModelProperty(value = "回调地址", position = 7)
    private String notifyUrl;
    @ApiModelProperty(value = "页面跳转地址", position = 7)
    private String returnUrl;
    @ApiModelProperty(value = "支付请求地址", position = 7)
    private String submitUrl;


}
