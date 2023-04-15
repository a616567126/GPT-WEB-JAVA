package com.chat.java.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shenshipeng
 * @date 2022-11-11 09:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AvoidRepeatRequest {

    /** 请求间隔时间，单位秒，该时间范围内的请求为重复请求 */
    long intervalTime() default 60 * 60L;
    /** 返回的提示信息 */
    String msg() default "请不要频繁重复请求！";
}
