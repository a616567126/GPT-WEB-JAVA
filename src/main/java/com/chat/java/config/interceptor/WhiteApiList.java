package com.chat.java.config.interceptor;

import org.springframework.util.DigestUtils;

import java.util.*;

/**
 * @author shenshipeng
 * @date 2022/04/22 14:03
 */
public class WhiteApiList {
    public static List<String> list = new ArrayList<>();
    static {

        //登录
        list.add("/user/token/login");
        list.add("/user/token/admin/login");
        //注册
        list.add("/user/token/register");
        list.add("/user/token/register/msm");
        list.add("/user/token/register/email");
        //支付回调
        list.add("/order/callback");
        list.add("/order/ali/callBack");
        list.add("/order/wx/callBack");


        //swagger
        list.add("/swagger-resources");
        list.add("/doc.html");
        list.add("/webjars/**");

        //wx
        list.add("/wx/callBack");
        list.add("/wx/getTicket");
//        list.add("/wx/uploadPermanent");
//        list.add("/wx/uploadTemp");

        //短信
        list.add("/user/token/send/msg");
        //邮箱
        list.add("/user/token/send/mail");
        //获取注册方式
        list.add("/user/token/get/register/method");
    }

}
