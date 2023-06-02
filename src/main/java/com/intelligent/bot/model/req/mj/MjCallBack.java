package com.intelligent.bot.model.req.mj;

import com.intelligent.bot.enums.mj.TaskStatus;
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

    private TaskStatus status;

    private String notifyHook;
}
