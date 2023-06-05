package com.intelligent.bot.model.req.mj;

import com.intelligent.bot.enums.mj.TaskAction;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 提交UV任务参数
 */
@Data
public class UVSubmitReq {

	@NotNull
	private Long id;

	@NotNull
	private TaskAction taskAction;

	@NotNull
	private Integer index;

}
