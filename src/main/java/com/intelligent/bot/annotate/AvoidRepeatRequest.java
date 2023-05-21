package com.intelligent.bot.annotate;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AvoidRepeatRequest {

    /** 请求间隔时间，单位秒，该时间范围内的请求为重复请求 */
    long intervalTime() default  0L;
    /** 返回的提示信息 */
    String msg() default "请不要频繁重复请求！";
}
