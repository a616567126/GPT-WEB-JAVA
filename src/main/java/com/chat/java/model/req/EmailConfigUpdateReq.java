package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EmailConfigUpdateReq {

    @ApiModelProperty(value = "邮件提供商地址")
    private String host;

    @ApiModelProperty(value = "端口号")
    private Integer port;

    @ApiModelProperty(value = "邮件账号")
    private String username;

    @ApiModelProperty(value = "SMTP授权密码")
    private String password;

    @ApiModelProperty(value = "邮件协议")
    private String protocol;

    @NotNull(message = "id不能为空")
    private Long id;

}
