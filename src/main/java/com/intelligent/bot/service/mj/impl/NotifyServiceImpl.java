package com.intelligent.bot.service.mj.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.mj.Task;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public void notifyTaskChange(Task task) {
		if (CharSequenceUtil.isBlank(task.getNotifyHook())) {
			return;
		}
		try {
			String paramsStr = OBJECT_MAPPER.writeValueAsString(task);
			log.debug("任务变更, 触发推送, task: {}", paramsStr);
			postJson(paramsStr);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void postJson(String paramsJson) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(paramsJson, headers);
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		try {
			log.error("回调参数：{}",paramsJson);
			ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(sysConfig.getApiUrl() + CommonConst.MJ_CALL_BACK_URL, httpEntity, String.class);
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return;
			}
			log.warn("回调通知接口失败, code: {}, msg: {}", responseEntity.getStatusCodeValue(), responseEntity.getBody());
		} catch (RestClientException e) {
			log.warn("回调通知接口失败, {}", e.getMessage());
		}
	}

}
