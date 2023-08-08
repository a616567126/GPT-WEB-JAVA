package com.intelligent.bot.config;

import com.intelligent.bot.server.wss.WebSocketStarter;
import com.intelligent.bot.server.wss.user.UserWebSocketStarter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

;

@Configuration
public class BeanConfig {


	@Bean
	WebSocketStarter webSocketStarter() {
		return new UserWebSocketStarter();
	}

	@Bean
	ApplicationRunner enableMetaChangeReceiverInitializer(WebSocketStarter webSocketStarter) {
		return args -> webSocketStarter.start();
	}


}
