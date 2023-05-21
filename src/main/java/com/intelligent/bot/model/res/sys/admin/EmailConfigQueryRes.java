package com.intelligent.bot.model.res.sys.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailConfigQueryRes {

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

    /**
     * id
      */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
