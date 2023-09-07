package com.intelligent.bot.model.mj.doman;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DiscordAccount extends DomainObject {

	/**
	 * 服务器ID
	 */
	private String guildId;

	/**
	 * 频道ID
	 */
	private String channelId;

	/**
	 * 用户Token
	 */
	private String userToken;


	/**
	 * 是否可用 0 可用 1禁用.
	 */
	private Integer state;

	/**
	 * 并发数
	 */
	private int coreSize = 3;

	/**
	 * 等待队列长度
	 */
	private int queueSize = 10;

	/**
	 * 任务超时时间(分钟)
	 */
	private int timeoutMinutes = 5;

	@JsonIgnore
	public String getDisplay() {
		return this.channelId;
	}
}
