package com.intelligent.bot.api.mj.support;

import com.intelligent.bot.api.mj.support.handle.DescribeMessageHandler;
import com.intelligent.bot.api.mj.support.handle.ImagineMessageHandler;
import com.intelligent.bot.api.mj.support.handle.UVMessageHandler;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordMessageListener extends ListenerAdapter {
	private final ImagineMessageHandler imagineMessageHandler;
	private final UVMessageHandler uvMessageHandler;
	private final DescribeMessageHandler describeMessageHandler;

	private boolean ignoreAndLogMessage(Message message, String eventName) {
		SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
		String channelId = message.getChannel().getId();
		if (!sysConfig.getMjChannelId().equals(channelId)) {
			return true;
		}
		String authorName = message.getAuthor().getName();
		log.debug("{} - {}: {}", eventName, authorName, message.getContentRaw());
		return !sysConfig.getMjBotName().equals(authorName);
	}

	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {
		Message message = event.getMessage();
		if (ignoreAndLogMessage(message, "消息变更")) {
			return;
		}
		if (message.getInteraction() != null && "describe".equals(message.getInteraction().getName())) {
			this.describeMessageHandler.onMessageUpdate(message);
		} else {
			this.uvMessageHandler.onMessageUpdate(message);
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		if (ignoreAndLogMessage(message, "消息接收")) {
			return;
		}
		if (MessageType.SLASH_COMMAND.equals(message.getType()) || MessageType.DEFAULT.equals(message.getType())) {
			this.imagineMessageHandler.onMessageReceived(message);
		} else if (MessageType.INLINE_REPLY.equals(message.getType()) && message.getReferencedMessage() != null) {
			this.uvMessageHandler.onMessageReceived(message);
		}
	}

}
