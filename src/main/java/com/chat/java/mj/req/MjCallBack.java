package com.chat.java.mj.req;

import lombok.Data;

/**
 * ClassName:MjCallBack
 * Package:com.cn.app.chatgptbot.mj.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/5/8 - 13:46
 * @Version: v1.0
 */
@Data
public class MjCallBack {

    private String action;

    private String id;

    private String prompt;

    private String description;

    private String state;

    private Long submitTime;

    private Long finishTime;

    private String imageUrl;

    private String status;

    private String notifyHook;
}
