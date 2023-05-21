package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("email_config")
public class EmailConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 邮件提供商地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 邮件账号
     */
    private String username;

    /**
     * SMTP授权密码
     */
    private String password;

    /**
     * 邮件协议
     */
    private String protocol;

}
