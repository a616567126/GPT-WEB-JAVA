package com.intelligent.bot.server.wss.user;


import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.model.mj.doman.DiscordAccount;
import com.intelligent.bot.server.wss.handle.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.List;

@Slf4j
public class UserMessageListener{
	private final DiscordAccount account;
	private final List<MessageHandler> messageHandlers;

	public UserMessageListener(DiscordAccount account, List<MessageHandler> messageHandlers) {
		this.account = account;
		this.messageHandlers = messageHandlers;
	}

	public void onMessage(DataObject raw) throws WxErrorException {
		MessageType messageType = MessageType.of(raw.getString("t"));
		if (messageType == null || MessageType.DELETE == messageType) {
			return;
		}
		DataObject data = raw.getObject("d");
		if (ignoreAndLogMessage(data, messageType)) {
			return;
		}
		ThreadUtil.sleep(50);
		for (MessageHandler messageHandler : this.messageHandlers) {
			messageHandler.handle(messageType, data);
		}
	}

	private boolean ignoreAndLogMessage(DataObject data, MessageType messageType) {
		String channelId = data.getString("channel_id");
		if (!CharSequenceUtil.equals(channelId, this.account.getChannelId())) {
			return true;
		}
		String authorName = data.optObject("author").map(a -> a.getString("username")).orElse("System");
		log.debug("{} - {} - {}: {}", this.account.getDisplay(), messageType.name(), authorName, data.opt("content").orElse(""));
		return false;
	}
}