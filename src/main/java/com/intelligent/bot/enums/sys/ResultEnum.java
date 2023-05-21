package com.intelligent.bot.enums.sys;


import lombok.Getter;

@Getter
public enum ResultEnum {
    SUCCESS(200,"接口响应成功"),

    FAIL(1,"接口响应失败"),

    ERROR(10001,"系统异常"),

    ;
    private final Integer code;

    private final String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
