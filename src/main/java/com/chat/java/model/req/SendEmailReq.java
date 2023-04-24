package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class SendEmailReq {

    @ApiModelProperty(value = "邮件")
    @NotNull(message = "邮件不能为空")
    private String email;


}
