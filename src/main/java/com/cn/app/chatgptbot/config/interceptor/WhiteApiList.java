package com.cn.app.chatgptbot.config.interceptor;

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
        //支付回调
        list.add("/order/callback");
        list.add("/order/ali/callBack");

        //swagger
        list.add("/swagger-resources");
        list.add("/doc.html");
        list.add("/webjars/**");
    }

}
