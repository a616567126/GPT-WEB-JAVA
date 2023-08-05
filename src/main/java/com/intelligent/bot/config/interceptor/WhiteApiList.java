package com.intelligent.bot.config.interceptor;

import java.util.ArrayList;
import java.util.List;


public class WhiteApiList {
    public static List<String> list = new ArrayList<>();
    static {
        //授权
        list.add("/auth/temp");
        list.add("/auth/login");
        list.add("/auth/admin/login");
        //注册
        list.add("/auth/register");
        list.add("/auth/register/email");
        //发送消息
        list.add("/send/email");
        //易支付回调
        list.add("/order/yi/callback");
        //微信支付回调
        list.add("/order/wx/callback");
        //sse
        list.add("/sse/**");
        //获取注册方式
        list.add("/client/register/method");
        //mj回调
        list.add("/mj/callBack");
        //wx
        list.add("/wx/callBack");
        //获取客户端配置
        list.add("/client/client/conf");
        //微信获取二维码
        list.add("/wx/getTicket");
        //画廊
        list.add("/client/gallery");
    }
}
