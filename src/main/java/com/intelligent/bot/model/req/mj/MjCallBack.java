package com.intelligent.bot.model.req.mj;

import lombok.Data;


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
