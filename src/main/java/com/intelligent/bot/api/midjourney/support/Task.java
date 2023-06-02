package com.intelligent.bot.api.midjourney.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intelligent.bot.enums.mj.Action;
import com.intelligent.bot.enums.mj.TaskStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务
 */
@Data
public class Task implements Serializable {

	private static final long serialVersionUID = -674915748204390789L;

	private Action action;
	/**
	 * 任务ID
	 */
	private String id;
	/**
	 * 提示词
	 */
	private String prompt;

	/**
	 * 提示词-英文
	 */
	private String promptEn;

	/**
	 * 任务描述
	 */
	private String description;

	/**
	 * 自定义参数
	 */
	private String state;

	/**
	 * 提交时间
	 */
	private Long submitTime;

	/**
	 * 开始执行时间
	 */
	private Long startTime;

	/**
	 * 结束时间
	 */
	private Long finishTime;

	/**
	 * 图片url
	 */
	private String imageUrl;

	/**
	 * 任务状态
	 */
	private TaskStatus status = TaskStatus.NOT_START;

	/**
	 * 失败原因
	 */
	private String failReason;

	// Hidden -- start
	private String key;

	private String finalPrompt;

	private String notifyHook;

	private String relatedTaskId;

	private String messageId;

	private String messageHash;

	private Integer errorNumber = 0;
	// Hidden -- end

	@JsonIgnore
	private final transient Object lock = new Object();

	public void sleep() throws InterruptedException {
		synchronized (this.lock) {
			this.lock.wait();
		}
	}

	public void awake() {
		synchronized (this.lock) {
			this.lock.notifyAll();
		}
	}

}
