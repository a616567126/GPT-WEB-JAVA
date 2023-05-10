package com.chat.java.mj.service;


import com.chat.java.mj.result.Message;

public interface DiscordService {

	Message<Void> imagine(String prompt);

	Message<Void> upscale(String messageId, int index, String messageHash);

	Message<Void> variation(String messageId, int index, String messageHash);

	Message<Void> reset(String messageId, String messageHash);

}