package com.chat.java.model.req;

import com.chat.java.model.base.BasePageHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
public class EmailConfigAddReq {

    @ApiModelProperty(value = "邮件提供商地址")
    @NotNull(message = "邮件提供商地址：不能为空")
    private String host;

    @ApiModelProperty(value = "端口号")
    @NotNull(message = "端口号：不能为空")
    private Integer port;

    @ApiModelProperty(value = "邮件账号")
    @NotNull(message = "邮件账号：不能为空")
    private String username;

    @ApiModelProperty(value = "SMTP授权密码")
    @NotNull(message = "SMTP授权密码：不能为空")
    private String password;

    @ApiModelProperty(value = "邮件协议")
    @NotNull(message = "邮件协议：不能为空")
    private String protocol;

}
