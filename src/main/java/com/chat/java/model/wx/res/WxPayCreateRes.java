package com.chat.java.model.wx.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class WxPayCreateRes {

    @ApiModelProperty(value = "二维码地址")
    private String codeUrl;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal price;

}
