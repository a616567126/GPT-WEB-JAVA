package com.intelligent.bot.service.mj.impl;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.service.mj.DiscordService;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordServiceImpl implements DiscordService {

	private static final String DISCORD_API_URL = "https://discord.com/api/v9/interactions";
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36";

	private String imagineParamsJson;
	private String upscaleParamsJson;
	private String variationParamsJson;
	private String resetParamsJson;
	

	@PostConstruct
	void init() {
		try {
			this.imagineParamsJson = IoUtil.readUtf8(ResourceUtils.getURL("classpath:api-params/imagine.json").openStream());
			this.upscaleParamsJson = IoUtil.readUtf8(ResourceUtils.getURL("classpath:api-params/upscale.json").openStream());
			this.variationParamsJson = IoUtil.readUtf8(ResourceUtils.getURL("classpath:api-params/variation.json").openStream());
			this.resetParamsJson = IoUtil.readUtf8(ResourceUtils.getURL("classpath:api-params/reset.json").openStream());
		} catch (IOException e) {
			// can't happen
		}
	}

	@Override
	public B<Void> imagine(String prompt) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String paramsStr = this.imagineParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId());
		JSONObject params = JSONObject.parseObject(paramsStr);
		params.getJSONObject("data").getJSONArray("options").getJSONObject(0)
				.put("value", prompt);
		return postJson(params.toString());
	}

	@Override
	public B<Void> upscale(String messageId, int index, String messageHash) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String paramsStr = this.upscaleParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		return postJson(paramsStr);
	}

	@Override
	public B<Void> variation(String messageId, int index, String messageHash) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String paramsStr = this.variationParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		return postJson(paramsStr);
	}

	@Override
	public B<Void> reset(String messageId, String messageHash) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String paramsStr = this.variationParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$message_id", messageId)
				.replace("$message_hash", messageHash);
		return postJson(paramsStr);
	}

	private B<Void> postJson(String paramsStr) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", sysConfig.getMjUserToken());
		headers.add("user-agent", USER_AGENT);
		HttpEntity<String> httpEntity = new HttpEntity<>(paramsStr, headers);
		try {
			ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(DISCORD_API_URL, httpEntity, String.class);
			if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
				return B.okBuild();
			}
			return B.build(responseEntity.getStatusCodeValue(), CharSequenceUtil.sub(responseEntity.getBody(), 0, 100));
		} catch (HttpClientErrorException e) {
			try {
				JSONObject error = JSONObject.parseObject(e.getResponseBodyAsString());
				return B.build(error.getInteger("code"), error.getString("message"));
			} catch (Exception je) {
				return B.build(e.getRawStatusCode(), CharSequenceUtil.sub(e.getMessage(), 0, 100));
			}
		}
	}
}
