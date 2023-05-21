package com.intelligent.bot.model.res.sys;

import lombok.Data;

@Data
public class ClientHomeLogRes {

    /**
     * 消息标题，取第一次提问的内容
     */
    private String title;

    /**
     * 全部消息内容
     */
    private String content;

    /**
     * 消息id
     */
    private Long id;
}
