package com.intelligent.bot.model.req.mj;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TaskReq {

	@NotNull
	private Long id;
}
