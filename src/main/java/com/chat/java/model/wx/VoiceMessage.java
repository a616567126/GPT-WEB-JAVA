package com.chat.java.model.wx;

import lombok.Data;


@Data
public class VoiceMessage extends BaseMessage {
	// 语音
	private Voice Voice;

}
