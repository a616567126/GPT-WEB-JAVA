package com.cn.app.chatgptbot.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shenshipeng
 * @date 2022/04/22 14:03
 */
public class PostAdminApiList {
    public static List<String> list = new ArrayList<>();
    static {
        list.add("/user/token/register");
        list.add("/user/token/login");
        list.add("/product/list");
        list.add("/user/token/home");
        list.add("/v1/chat/turbo");
        list.add("/user/token/getType");
        list.add("/use/log/resetLog");
        list.add("/announcement/queryPage");
        list.add("/order/create");

    }

}
