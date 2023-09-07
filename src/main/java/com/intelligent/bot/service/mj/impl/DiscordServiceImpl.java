package com.intelligent.bot.service.mj.impl;


import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.BlendDimensions;
import com.intelligent.bot.model.mj.doman.DiscordAccount;
import com.intelligent.bot.service.mj.DiscordService;
import eu.maxschuster.dataurl.DataUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class DiscordServiceImpl implements DiscordService {

	private final DiscordAccount account;

	private final Map<String, String> paramsMap;

	private final RestTemplate restTemplate;
	
	public DiscordServiceImpl(DiscordAccount account, RestTemplate restTemplate, Map<String, String> paramsMap) {
		this.account = account;
		this.paramsMap = paramsMap;
		this.restTemplate = restTemplate;
	}

	@Override
	public B<Void> imagine(String prompt, String nonce) {
		String paramsStr = replaceInteractionParams(this.paramsMap.get("imagine"), nonce);
		JSONObject params = JSONObject.parseObject(paramsStr);
		params.getJSONObject("data").getJSONArray("options").getJSONObject(0)
				.put("value", prompt);
		return postJsonAndCheckStatus(params.toString());
	}

	@Override
	public B<Void> upscale(String messageId, int index, String messageHash, int messageFlags, String nonce) {
		String paramsStr = replaceInteractionParams(this.paramsMap.get("upscale"), nonce)
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		JSONObject jsonObject = JSONObject.parseObject(paramsStr);
		jsonObject.put("message_flags", messageFlags);
		paramsStr =jsonObject.toString();
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> variation(String messageId, int index, String messageHash, int messageFlags, String nonce) {
		String paramsStr = replaceInteractionParams(this.paramsMap.get("variation"), nonce)
				.replace("$message_id", messageId)
				.replace("$index", String.valueOf(index))
				.replace("$message_hash", messageHash);
		JSONObject jsonObject = JSONObject.parseObject(paramsStr);
		jsonObject.put("message_flags", messageFlags);
		paramsStr =jsonObject.toString();
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> reroll(String messageId, String messageHash, int messageFlags, String nonce) {
		String paramsStr = replaceInteractionParams(this.paramsMap.get("reroll"), nonce)
				.replace("$message_id", messageId)
				.replace("$message_hash", messageHash);
		JSONObject jsonObject = JSONObject.parseObject(paramsStr);
		jsonObject.put("message_flags", messageFlags);
		paramsStr =jsonObject.toString();
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> describe(String finalFileName, String nonce) {
		String fileName = CharSequenceUtil.subAfter(finalFileName, "/", true);
		String paramsStr = replaceInteractionParams(this.paramsMap.get("describe"), nonce)
				.replace("$file_name", fileName)
				.replace("$final_file_name", finalFileName);
		return postJsonAndCheckStatus(paramsStr);
	}

	@Override
	public B<Void> blend(List<String> finalFileNames, BlendDimensions dimensions, String nonce) {
		String paramsStr = replaceInteractionParams(this.paramsMap.get("blend"), nonce);
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
			JSONArray files = new JSONArray();
			files.add(fileObj);
			params.put("files", files);
			ResponseEntity<String> responseEntity = postJson(String.format(CommonConst.DISCORD_UPLOAD_URL,this.account.getChannelId()), params.toString());
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
		String fileName = CharSequenceUtil.subAfter(finalFileName, "/", true);
		String paramsStr = this.paramsMap.get("message").replace("$content", content)
				.replace("$channel_id", this.account.getChannelId())
				.replace("$file_name", fileName)
				.replace("$final_file_name", finalFileName);
		ResponseEntity<String> responseEntity = postJson(String.format(CommonConst.DISCORD_MESSAGE_URL, this.account.getChannelId(), paramsStr));
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
		this.restTemplate.put(uploadUrl, requestEntity);
	}

	private ResponseEntity<String> postJson(String paramsStr) {
		return postJson(CommonConst.DISCORD_API_URL, paramsStr);
	}

	private ResponseEntity<String> postJson(String url, String paramsStr) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", this.account.getUserToken());
		headers.set("User-Agent", CommonConst.USERAGENT);
		HttpEntity<String> httpEntity = new HttpEntity<>(paramsStr, headers);
		return this.restTemplate.postForEntity(url, httpEntity, String.class);
	}

	private B<Void> postJsonAndCheckStatus(String paramsStr) {
		try {
			ResponseEntity<String> responseEntity = postJson(paramsStr);
			if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
				return B.okBuild();
			}
			return B.finalBuild( CharSequenceUtil.sub(responseEntity.getBody(), 0, 100));
		} catch (HttpStatusCodeException e) {
			return convertHttpStatusCodeException(e);
		}
	}

	private B<Void> convertHttpStatusCodeException(HttpStatusCodeException e) {
		try {
			JSONObject error = JSONObject.parseObject(e.getResponseBodyAsString());
			return B.finalBuild(error.getString("message"));
		} catch (Exception je) {
			return B.finalBuild(CharSequenceUtil.sub(e.getMessage(), 0, 100));
		}
	}

	private String replaceInteractionParams(String paramsStr, String nonce) {
		return paramsStr.replace("$guild_id", this.account.getGuildId())
				.replace("$channel_id", this.account.getChannelId())
				.replace("$session_id", CommonConst.MJ_SESSION_ID)
				.replace("$nonce", nonce);
	}

	
}
