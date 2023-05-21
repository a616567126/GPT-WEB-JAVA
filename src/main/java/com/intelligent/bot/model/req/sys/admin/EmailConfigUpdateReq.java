package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;

@Data
public class EmailConfigUpdateReq {

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

}
