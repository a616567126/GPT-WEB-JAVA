package com.intelligent.bot.model.req.sys;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ClientEmptyLog {

    /**
     * 消息类型  1-gpt对话 2-gpt画图 3-sd画图 4-fs画图 5-mj画图 6-bing对话
     */
    @NotNull
    private Integer sendType;
}
