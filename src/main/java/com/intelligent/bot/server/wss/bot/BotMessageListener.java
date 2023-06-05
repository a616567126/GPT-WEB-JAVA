package com.intelligent.bot.server.wss.bot;

import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.server.wss.handle.MessageHandler;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BotMessageListener extends ListenerAdapter implements ApplicationListener<ApplicationStartedEvent> {

	private final List<MessageHandler> messageHandlers = new ArrayList<>();

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		this.messageHandlers.addAll(event.getApplicationContext().getBeansOfType(MessageHandler.class).values());
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		if (ignoreAndLogMessage(message, MessageType.CREATE)) {
			return;
		}
		for (MessageHandler messageHandler : this.messageHandlers) {
			try {
				messageHandler.handle(MessageType.CREATE, message);
			} catch (IOException e) {
				throw new E(e.getMessage());
			}
		}
	}

	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {
		Message message = event.getMessage();
		if (ignoreAndLogMessage(message, MessageType.UPDATE)) {
			return;
		}
		for (MessageHandler messageHandler : this.messageHandlers) {
			try {
				messageHandler.handle(MessageType.UPDATE, message);
			} catch (IOException e) {
				throw new E(e.getMessage());
			}
		}
	}

	private boolean ignoreAndLogMessage(Message message, MessageType messageType) {
		String channelId = message.getChannel().getId();
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);

		if (!sysConfig.getMjChannelId().equals(channelId)) {
			return true;
		}
		String authorName = message.getAuthor().getName();
		log.debug("{} - {}: {}", messageType.name(), authorName, message.getContentRaw());
		return !sysConfig.getMjBotName().equals(authorName);
	}

}
