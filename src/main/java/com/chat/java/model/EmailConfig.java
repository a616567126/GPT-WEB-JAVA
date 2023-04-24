package com.chat.java.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("email_config")
public class EmailConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

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

}
