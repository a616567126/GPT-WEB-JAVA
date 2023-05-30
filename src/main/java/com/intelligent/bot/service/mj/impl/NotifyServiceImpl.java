package com.intelligent.bot.service.mj.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.bot.api.mj.support.Task;
import com.intelligent.bot.service.mj.NotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public void notifyTaskChange(Task task) {
		String notifyHook = task.getNotifyHook();
		if (CharSequenceUtil.isBlank(notifyHook)) {
			return;
		}
		try {
			String paramsStr = OBJECT_MAPPER.writeValueAsString(task);
			log.debug("任务变更, 触发推送, task: {}", paramsStr);
			postJson(notifyHook, paramsStr);
		} catch (Exception e) {
			log.warn("回调通知接口失败: {}", e.getMessage());
		}
	}

	private void postJson(String notifyHook, String paramsJson) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(paramsJson, headers);
		ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(notifyHook, httpEntity, String.class);
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			return;
		}
		log.warn("回调通知接口失败, code: {}, msg: {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
	}

}
