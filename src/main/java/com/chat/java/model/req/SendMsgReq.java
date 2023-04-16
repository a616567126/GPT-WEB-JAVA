package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class SendMsgReq {

    @ApiModelProperty(value = "手机号")
    @NotNull(message = "手机号不能为空")
    private String mobile;


}
