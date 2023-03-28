package com.cn.app.chatgptbot.base;

import lombok.Getter;


@Getter
public enum ResultEnum {

    SUCCESS(200,"接口响应成功"),
    FAIL(1,"接口响应失败"),

    ;
    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
