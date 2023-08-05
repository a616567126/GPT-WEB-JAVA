package com.intelligent.bot.model.req.mj;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PrivateTask {

	@NotNull
	private Long id;

	/**
	 * 公开状态 0-公开、1-私有
	 */
	private Integer publicStatus;

}
