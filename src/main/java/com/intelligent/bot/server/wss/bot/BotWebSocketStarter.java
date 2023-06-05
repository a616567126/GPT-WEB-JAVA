package com.intelligent.bot.server.wss.bot;

import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.server.wss.WebSocketStarter;
import com.intelligent.bot.utils.sys.RedisUtil;
import com.neovisionaries.ws.client.WebSocketFactory;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import javax.annotation.Resource;

public class BotWebSocketStarter implements WebSocketStarter {
	@Resource
	private BotMessageListener botMessageListener;


	@Override
	public void start() throws Exception {
		SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(
				cacheObject.getMjBotToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
		builder.addEventListeners(this.botMessageListener);
		WebSocketFactory webSocketFactory = createWebSocketFactory();
		builder.setWebsocketFactory(webSocketFactory);
		builder.setSessionController(new CustomSessionController(CommonConst.MJ_WSS_URL));
		builder.setRestConfigProvider(value -> new RestConfig().setBaseUrl(CommonConst.DISCORD_SERVER_URL + "/api/v10/"));
		builder.build();
	}
}
