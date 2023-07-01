package com.intelligent.bot.service.mj.impl;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.api.midjourney.support.DiscordHelper;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.BlendDimensions;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.service.mj.DiscordService;
import com.intelligent.bot.utils.gpt.Proxys;
import com.intelligent.bot.utils.sys.RedisUtil;
import eu.maxschuster.dataurl.DataUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.Proxy;
import java.util.List;

@Slf4j
@Service
public class DiscordServiceImpl implements DiscordService {

	@Resource
	DiscordHelper discordHelper;

	private String imagineParamsJson;
	private String upscaleParamsJson;
	private String variationParamsJson;
	private String rerollParamsJson;
	private String describeParamsJson;
	private String blendParamsJson;

	private String messageParamsJson;




	@PostConstruct
	void init() {
		this.imagineParamsJson = ResourceUtil.readUtf8Str("api-params/imagine.json");
		this.upscaleParamsJson = ResourceUtil.readUtf8Str("api-params/upscale.json");
		this.variationParamsJson = ResourceUtil.readUtf8Str("api-params/variation.json");
		this.rerollParamsJson = ResourceUtil.readUtf8Str("api-params/reroll.json");
		this.describeParamsJson = ResourceUtil.readUtf8Str("api-params/describe.json");
		this.blendParamsJson = ResourceUtil.readUtf8Str("api-params/blend.json");
		this.messageParamsJson = ResourceUtil.readUtf8Str("api-params/message.json");
	}

	@Override
	public B<Void> imagine(String prompt) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String paramsStr = this.imagineParamsJson.replace("$guild_id",  sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$session_id", CommonConst.MJ_SESSION_ID);
		JSONObject params = JSONObject.parseObject(paramsStr);
		params.getJSONObject("data").getJSONArray("options").getJSONObject(0)
				.put("value", prompt);
		return postJsonAndCheckStatus(params.toString());
	}

	@Override
	public B<Void> upscale(String messageId, int index, String messageHash, int messageFlags) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String paramsStr = this.upscaleParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$session_id", CommonConst.MJ_SESSION_ID)
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		JSONObject jsonObject = JSONObject.parseObject(paramsStr);
		jsonObject.put("message_flags", messageFlags);
		paramsStr =jsonObject.toString();
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> variation(String messageId, int index, String messageHash, int messageFlags) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String paramsStr = this.variationParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$session_id", CommonConst.MJ_SESSION_ID)
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		JSONObject jsonObject = JSONObject.parseObject(paramsStr);
		jsonObject.put("message_flags", messageFlags);
		paramsStr =jsonObject.toString();
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> reroll(String messageId, String messageHash, int messageFlags) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String paramsStr = this.rerollParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$session_id", CommonConst.MJ_SESSION_ID)
				.replace("$message_id", messageId)
				.replace("$message_hash", messageHash);
		JSONObject jsonObject = JSONObject.parseObject(paramsStr);
		jsonObject.put("message_flags", messageFlags);
		paramsStr =jsonObject.toString();
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> describe(String finalFileName) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String fileName = CharSequenceUtil.subAfter(finalFileName, "/", true);
		String paramsStr = this.describeParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$session_id", CommonConst.MJ_SESSION_ID)
				.replace("$file_name", fileName)
				.replace("$final_file_name", finalFileName);
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> blend(List<String> finalFileNames, BlendDimensions dimensions) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String paramsStr = this.blendParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$session_id", CommonConst.MJ_SESSION_ID);
		JSONObject params = JSONObject.parseObject(paramsStr);
		JSONArray options = params.getJSONObject("data").getJSONArray("options");
		JSONArray attachments = params.getJSONObject("data").getJSONArray("attachments");
		for (int i = 0; i < finalFileNames.size(); i++) {
			String finalFileName = finalFileNames.get(i);
			String fileName = CharSequenceUtil.subAfter(finalFileName, "/", true);
			JSONObject attachment = new JSONObject();
			attachment.put("id", String.valueOf(i));
			attachment.put("filename", fileName);
			attachment.put("uploaded_filename", finalFileName);
			attachments.add(attachment);
			JSONObject option = new JSONObject();
			option.put("type", 11);
			option.put("name", "image" + (i + 1));
			option.put("value", i);
			options.add(option);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", 3);
		jsonObject.put("name", "dimensions");
		jsonObject.put("value", "--ar " + dimensions.getValue());
		options.add(jsonObject);
		return postJsonAndCheckStatus(params.toString());
	}

	@Override
	public B<String> upload(String fileName, DataUrl dataUrl) {
		try {
			JSONObject fileObj = new JSONObject();
			fileObj.put("filename", fileName);
			fileObj.put("file_size", dataUrl.getData().length);
			fileObj.put("id", "0");
			JSONObject params = new JSONObject();
			params.put("files", new JSONArray().add(fileObj));
			ResponseEntity<String> responseEntity = postJson(CommonConst.DISCORD_UPLOAD_URL, params.toString());
			if (responseEntity.getStatusCode() != HttpStatus.OK) {
				log.error("上传图片到discord失败, status: {}, msg: {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
				return B.finalBuild("上传图片到discord失败");
			}

			JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
			JSONArray array = jsonObject.getJSONArray("attachments");
			if (array.size() == 0) {
				return B.finalBuild("上传图片到discord失败");
			}
			String uploadUrl = array.getJSONObject(0).getString("upload_url");
			String uploadFilename = array.getJSONObject(0).getString("upload_filename");
			putFile(uploadUrl, dataUrl);
			return B.okBuild(uploadFilename);
		} catch (Exception e) {
			log.error("上传图片到discord失败", e);
			return B.finalBuild("上传图片到discord失败");
		}
	}

	@Override
	public B<String> sendImageMessage(String content, String finalFileName) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		String fileName = CharSequenceUtil.subAfter(finalFileName, "/", true);
		String paramsStr = this.messageParamsJson.replace("$content", content)
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$file_name", fileName)
				.replace("$final_file_name", finalFileName);
		ResponseEntity<String> responseEntity = postJson(String.format(CommonConst.DISCORD_MESSAGE_URL, sysConfig.getMjChannelId(), paramsStr));
		if (responseEntity.getStatusCode() != HttpStatus.OK) {
			log.error("发送图片消息到discord失败, status: {}, msg: {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
			return B.finalBuild("发送图片消息到discord失败");
		}
		JSONObject result = JSONObject.parseObject(responseEntity.getBody());
		JSONArray attachments = result.getJSONArray("attachments");
		if (!attachments.isEmpty()) {
			return B.okBuild(attachments.getJSONObject(0).getString("url"));
		}
		return B.finalBuild("发送图片消息到discord失败: 图片不存在");
	}


	private void putFile(String uploadUrl, DataUrl dataUrl) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Agent", CommonConst.USERAGENT);
		headers.setContentType(MediaType.valueOf(dataUrl.getMimeType()));
		headers.setContentLength(dataUrl.getData().length);
		HttpEntity<byte[]> requestEntity = new HttpEntity<>(dataUrl.getData(), headers);
		new RestTemplate().put(uploadUrl, requestEntity);
	}

	private ResponseEntity<String> postJson(String paramsStr) {
		return postJson(CommonConst.DISCORD_API_URL, paramsStr);
	}

	private ResponseEntity<String> postJson(String url, String paramsStr) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", sysConfig.getMjUserToken());
		headers.add("User-Agent", CommonConst.USERAGENT);
		HttpEntity<String> httpEntity = new HttpEntity<>(paramsStr, headers);
		if(null != sysConfig.getIsOpenProxy() && sysConfig.getIsOpenProxy() == 1){
			Proxy proxy  = Proxys.http(sysConfig.getProxyIp(), sysConfig.getProxyPort());
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			requestFactory.setProxy(proxy);
			return new RestTemplate(requestFactory).postForEntity(url, httpEntity, String.class);
		}else{
			return new RestTemplate().postForEntity(url, httpEntity, String.class);
		}
	}

	private B<Void> postJsonAndCheckStatus(String paramsStr) {
		try {
			ResponseEntity<String> responseEntity = postJson(paramsStr);
			if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
				return B.okBuild();
			}
			return B.finalBuild(CharSequenceUtil.sub(responseEntity.getBody(), 0, 100));
		} catch (HttpClientErrorException e) {
			try {
				JSONObject error = JSONObject.parseObject(e.getResponseBodyAsString());
				return B.finalBuild(error.getString("message"));
			} catch (Exception je) {
				return B.finalBuild(CharSequenceUtil.sub(e.getMessage(), 0, 100));
			}
		}
	}
}
