package com.intelligent.bot.service.mj.impl;


import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.service.mj.DiscordService;
import com.intelligent.bot.utils.sys.RedisUtil;
import eu.maxschuster.dataurl.DataUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordServiceImpl implements DiscordService {

	private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36";
	private String imagineParamsJson;
	private String upscaleParamsJson;
	private String variationParamsJson;
	private String resetParamsJson;
	private String describeParamsJson;


	@PostConstruct
	void init() {
		this.imagineParamsJson = ResourceUtil.readUtf8Str("api-params/imagine.json");
		this.upscaleParamsJson = ResourceUtil.readUtf8Str("api-params/upscale.json");
		this.variationParamsJson = ResourceUtil.readUtf8Str("api-params/variation.json");
		this.resetParamsJson = ResourceUtil.readUtf8Str("api-params/reset.json");
		this.describeParamsJson = ResourceUtil.readUtf8Str("api-params/describe.json");
	}

	@Override
	public B<Void> imagine(String prompt) {
		SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
		String paramsStr = this.imagineParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId());
		JSONObject params = JSONObject.parseObject(paramsStr);
		params.getJSONObject("data").getJSONArray("options").getJSONObject(0)
				.put("value", prompt);
		return postJsonAndCheckStatus(params.toString());
	}

	@Override
	public B<Void> upscale(String messageId, int index, String messageHash) {
		SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
		String paramsStr = this.upscaleParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> variation(String messageId, int index, String messageHash) {
		SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
		String paramsStr = this.variationParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> reset(String messageId, String messageHash) {
		SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
		String paramsStr = this.resetParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$message_id", messageId)
				.replace("$message_hash", messageHash);
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<String> upload(String fileName, DataUrl dataUrl) {
		try {
			SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
			JSONObject fileObj = new JSONObject();
			fileObj.put("filename", fileName);
			fileObj.put("file_size", dataUrl.getData().length);
			fileObj.put("id", "0");
			JSONObject params = new JSONObject();
			params.put("files", new JSONArray().add(fileObj));
			ResponseEntity<String> responseEntity = postJson(String.format(CommonConst.DISCORD_UPLOAD_URL,sysConfig.getMjChannelId()), params.toString());
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
	public B<Void> describe(String finalFileName) {
		SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
		String fileName = CharSequenceUtil.subAfter(finalFileName, "/", true);
		String paramsStr = this.describeParamsJson.replace("$guild_id", sysConfig.getMjGuildId())
				.replace("$channel_id", sysConfig.getMjChannelId())
				.replace("$file_name", fileName)
				.replace("$final_file_name", finalFileName);
		return postJsonAndCheckStatus(paramsStr);
	}

	private void putFile(String uploadUrl, DataUrl dataUrl) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Agent", this.userAgent);
		headers.setContentType(MediaType.valueOf(dataUrl.getMimeType()));
		headers.setContentLength(dataUrl.getData().length);
		HttpEntity<byte[]> requestEntity = new HttpEntity<>(dataUrl.getData(), headers);
		new RestTemplate().put(uploadUrl, requestEntity);
	}

	private ResponseEntity<String> postJson(String paramsStr) {
		return postJson(CommonConst.DISCORD_API_URL, paramsStr);
	}

	private ResponseEntity<String> postJson(String url, String paramsStr) {
		SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", sysConfig.getMjUserToken());
		headers.add("User-Agent", this.userAgent);
		HttpEntity<String> httpEntity = new HttpEntity<>(paramsStr, headers);
		return new RestTemplate().postForEntity(url, httpEntity, String.class);
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
