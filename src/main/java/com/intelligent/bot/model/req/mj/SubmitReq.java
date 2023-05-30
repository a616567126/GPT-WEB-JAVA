package com.intelligent.bot.model.req.mj;


import com.intelligent.bot.enums.mj.Action;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 提交Imagine或UV任务参数
 */
@Data
public class SubmitReq {
	/**
	 * 动作: IMAGINE\UPSCALE\VARIATION\RESET.
	 */
	private Action action;
	/**
	 * prompt: action 为 IMAGINE 必传.
	 */
	@NotNull
	private String prompt;

	/**
	 * 任务ID: action 为 UPSCALE\VARIATION\RESET 必传.
	 */
	private String taskId;

	/**
	 * index: action 为 UPSCALE\VARIATION 必传.
	 */
	private Integer index;

	private Long logId;

}
