package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("discord_account_config")
public class DiscordAccountConfig extends BaseEntity implements Serializable {

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
