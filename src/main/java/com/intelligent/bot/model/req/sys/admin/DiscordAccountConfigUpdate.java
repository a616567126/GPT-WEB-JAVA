package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;

@Data
public class DiscordAccountConfigUpdate {


    private Long id;

    /**
     * 服务器ID.
     */
    private String guildId;
    /**
     * 频道ID.
     */
    private String channelId;
    /**
     * 用户Token.
     */
    private String userToken;
    /**
     * 是是否可用 0 禁用 1启用.
     */
    private Integer state;
    /**
     * 并发数.
     */
    private int coreSize = 3;
    /**
     * 等待队列长度.
     */
    private int queueSize = 10;
    /**
     * 任务超时时间(分钟).
     */
    private int timeoutMinutes = 5;
    /**
     * 备注.
     */
    private String remark;
}
