package com.intelligent.bot.model.mj.data;

import com.intelligent.bot.enums.mj.Action;
import lombok.Data;

@Data
public class UVData {
	private String id;
	private Action action;
	private int index;
}
