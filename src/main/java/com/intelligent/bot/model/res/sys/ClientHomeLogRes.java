package com.intelligent.bot.model.res.sys;

import lombok.Data;

@Data
public class ClientHomeLogRes {

    /**
     * 消息标题，取第一次提问的内容
     */
    private String title;

    /**
     * mj TaskId
     */
    private String taskId;

    /**
     * 全部消息内容
     */
    private String content;

    /**
     * 消息id
     */
    private Long id;

    /**
     * 1-gpt对话 2-gpt画图 3-sd画图 4-fs画图 5-mj画图 6-bing 7-stableStudio 8-gpt4
     */
    private Integer sendType;

}
