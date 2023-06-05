package com.intelligent.bot.model.mj.data;

import com.intelligent.bot.enums.mj.TaskAction;
import lombok.Data;

@Data
public class MessageData {
	private TaskAction taskAction;
	private String prompt;
	private int index;
	private String status;
}
