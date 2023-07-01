package com.intelligent.bot.model.mj.data;

import com.intelligent.bot.enums.mj.TaskAction;
import lombok.Data;

@Data
public class TaskChangeParams {
	private Long id;
	private TaskAction action;
	private Integer index;
}
