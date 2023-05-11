package com.chat.java.mj.support;


import com.chat.java.model.SysConfig;
import com.chat.java.utils.RedisUtil;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DiscordStarter implements ApplicationListener<ApplicationStartedEvent> {
	@Resource
	private DiscordMessageListener discordMessageListener;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
		if(null != sysConfig.getIsOpenMj() && sysConfig.getIsOpenMj() == 1){
			DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(sysConfig.getMjBotToken(),
					GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
			builder.addEventListeners(this.discordMessageListener);
			builder.build();
		}

	}

}