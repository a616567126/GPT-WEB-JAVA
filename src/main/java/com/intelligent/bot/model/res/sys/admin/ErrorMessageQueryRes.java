package com.intelligent.bot.model.res.sys.admin;

import lombok.Data;


@Data
public class ErrorMessageQueryRes {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 异常内容
     */
    private String errorMessage;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 异常位置
     */
    private String position;


}
