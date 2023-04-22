package com.chat.java.model.wx.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;


@Data
public class WxPayCreateReq {

    @ApiModelProperty(value = "产品id")
    @NonNull
    private Long productId;

    @ApiModelProperty(value = "数量")
    @NonNull
    private Integer payNumber;

}
