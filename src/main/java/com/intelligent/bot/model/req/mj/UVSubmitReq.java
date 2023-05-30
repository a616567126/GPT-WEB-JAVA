package com.intelligent.bot.model.req.mj;

import com.intelligent.bot.enums.mj.Action;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 提交UV任务参数
 */
@Data
public class UVSubmitReq {

	@NotNull
	private String id;

	@NotNull
	private Action action;

	@NotNull
	private Integer index;

	@NotNull
	private Long logId;
}
