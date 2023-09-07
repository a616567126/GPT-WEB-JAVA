package com.intelligent.bot.api.midjourney.support;


import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.loadbalancer.DiscordInstance;
import com.intelligent.bot.api.midjourney.loadbalancer.DiscordInstanceImpl;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.mj.doman.DiscordAccount;
import com.intelligent.bot.server.wss.handle.MessageHandler;
import com.intelligent.bot.server.wss.user.UserMessageListener;
import com.intelligent.bot.server.wss.user.UserWebSocketStarter;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.mj.TaskStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DiscordAccountHelper {
	private final TaskStoreService taskStoreService;
	private final NotifyService notifyService;
	private final RestTemplate restTemplate;
	private final List<MessageHandler> messageHandlers;
	private final Map<String, String> paramsMap;

	public DiscordInstance createDiscordInstance(DiscordAccount account) {
		if (!CharSequenceUtil.isAllNotBlank(account.getGuildId(), account.getChannelId(), account.getUserToken())) {
			throw new IllegalArgumentException("guildId, channelId, userToken must not be blank");
		}
		UserMessageListener messageListener = new UserMessageListener(account, this.messageHandlers);
		UserWebSocketStarter webSocketStarter = new UserWebSocketStarter(CommonConst.MJ_WSS_URL, account, messageListener);
		return new DiscordInstanceImpl(account, webSocketStarter,this.restTemplate,
				this.taskStoreService,this.notifyService,this.paramsMap);
	}
}
