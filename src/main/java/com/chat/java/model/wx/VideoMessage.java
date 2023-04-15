package com.chat.java.model.wx;

import lombok.Data;

@Data
public class VideoMessage extends BaseMessage {
	// 视频
	private Video Video;
}
