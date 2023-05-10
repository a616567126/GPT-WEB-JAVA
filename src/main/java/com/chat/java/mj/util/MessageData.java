package com.chat.java.mj.util;

import com.chat.java.mj.enums.Action;
import lombok.Data;

@Data
public class MessageData {
	private Action action;
	private String prompt;
	private int index;
	private String status;
}
