package com.intelligent.bot.model.res.mj;

import com.intelligent.bot.enums.mj.Action;
import lombok.Data;

@Data
public class MessageData {
	private Action action;
	private String prompt;
	private int index;
	private String status;
}
