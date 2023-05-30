package com.intelligent.bot.api.mj.support;

import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.utils.sys.RedisUtil;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocketFactory;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DiscordStarter implements ApplicationListener<ApplicationStartedEvent> {

	@Value("${spring.profiles.active}")
	private String active;

	@Resource
	private DiscordMessageListener discordMessageListener;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
		if(null != sysConfig.getIsOpenMj() && sysConfig.getIsOpenMj() == 1){
			DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(sysConfig.getMjBotToken(),
					GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
			builder.addEventListeners(this.discordMessageListener);
			if (active.equals(CommonConst.ACTIVE) || sysConfig.getIsOpenProxy() == 1) {
				System.setProperty("http.proxyHost", sysConfig.getProxyIp());
				System.setProperty("http.proxyPort", String.valueOf(sysConfig.getProxyPort()));
				System.setProperty("https.proxyHost", sysConfig.getProxyIp());
				System.setProperty("https.proxyPort", String.valueOf(sysConfig.getProxyPort()));
				WebSocketFactory webSocketFactory = new WebSocketFactory();
				ProxySettings proxySettings = webSocketFactory.getProxySettings();
				proxySettings.setHost(sysConfig.getProxyIp());
				proxySettings.setPort(sysConfig.getProxyPort());
				builder.setWebsocketFactory(webSocketFactory);
			}
			builder.build();
		}

	}

}