package com.intelligent.bot.model.mj.data;

import com.intelligent.bot.enums.mj.TaskAction;
import lombok.Data;

@Data
public class UVData {
	private String id;
	private TaskAction taskAction;
	private int index;
}
