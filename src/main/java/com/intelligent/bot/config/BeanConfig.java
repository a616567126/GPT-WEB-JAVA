package com.intelligent.bot.config;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import com.intelligent.bot.api.midjourney.loadbalancer.rule.IRule;
import com.intelligent.bot.api.midjourney.support.DiscordAccountHelper;
import com.intelligent.bot.api.midjourney.support.DiscordHelper;
import com.intelligent.bot.config.mj.MjProperties;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.server.wss.handle.MessageHandler;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.mj.TaskStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

@Configuration
public class BeanConfig {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private MjProperties mjProperties;



	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}


	@Bean
	RedisTemplate<String, Task> taskRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Task> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Task.class));
		return redisTemplate;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	@Bean
	public IRule loadBalancerRule() {
		String ruleClassName = IRule.class.getPackage().getName() + "." + this.mjProperties.getAccountChooseRule();
		return ReflectUtil.newInstance(ruleClassName);
	}

	@Bean
	List<MessageHandler> messageHandlers() {
		return new ArrayList<>(this.applicationContext.getBeansOfType(MessageHandler.class).values());
	}

	@Bean
	DiscordAccountHelper discordAccountHelper(DiscordHelper discordHelper, TaskStoreService taskStoreService, NotifyService notifyService) throws IOException {
		Resource[] resources = this.applicationContext.getResources("classpath:api-params/*.json");
		Map<String, String> paramsMap = new HashMap<>();
		for (Resource resource : resources) {
			String filename = resource.getFilename();
			String params = IoUtil.readUtf8(resource.getInputStream());
			paramsMap.put(filename.substring(0, filename.length() - 5), params);
		}
		return new DiscordAccountHelper(taskStoreService, notifyService,restTemplate(), messageHandlers(), paramsMap);
	}

}
