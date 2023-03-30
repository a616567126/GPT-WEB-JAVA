package com.cn.app.chatgptbot.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.cn.app.chatgptbot.utils.DateUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class B<T> implements Serializable {

    @JSONField(ordinal= 1)
    private int status;
    @JSONField(ordinal= 2)
    private String message;
    @JSONField(ordinal= 3)
    private Date timestamp;
    @JSONField(ordinal= 4)
    private T data;

    private B(int status) {
        this.status = status;
        this.timestamp = DateUtil.getCurrentDateTime();
    }
    private B(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = DateUtil.getCurrentDateTime();
    }

    private B(int status, T data) {
        this.status = status;
        this.timestamp = DateUtil.getCurrentDateTime();
        this.data = data;
    }

    private B(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.timestamp = DateUtil.getCurrentDateTime();
        this.data = data;
    }

    private B(int status, String message, Date timestamp, T data) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }

    public static <T> B<T> build(int status, String message, T data) {
        return new B<>(status,message,data);
    }
    public static <T> B<T> okBuild(T data) {
        return new B<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),data);
    }
    public static <T> B<T> okBuild() {
        return new B<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    public static <T> B<T> finalBuild(String msg) {
        return new B<>(ResultEnum.FAIL.getCode(), msg);
    }

    public static <T> B<T> build(int status, String message) {
        return new B<>(status,message);
    }

    public static <T> B<T> build(int status, String message, Date timestamp, T data) {
        return new B<>(status, message, timestamp, data);
    }

}
