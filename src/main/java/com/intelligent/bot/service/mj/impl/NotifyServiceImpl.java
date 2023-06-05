package com.intelligent.bot.service.mj.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.MjTask;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.mj.MjCallBack;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.utils.sys.FileUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Resource
	AsyncService asyncService;
	@Override
	public void notifyTaskChange(MjTask task) {
		String notifyHook = task.getNotifyHook();
		if (CharSequenceUtil.isBlank(notifyHook)) {
			return;
		}
		try {
			String paramsStr = OBJECT_MAPPER.writeValueAsString(task);
//			log.info("任务变更, 触发推送, task: {}", paramsStr);
			postJson(notifyHook, paramsStr);
		} catch (Exception e) {
			log.warn("回调通知接口失败: {}", e.getMessage());
		}
	}

	private void postJson(String notifyHook, String paramsJson) throws IOException {
		MjCallBack mjTask = JSONObject.parseObject(paramsJson, MjCallBack.class);
		log.info("mj开始回调,回调内容：{}", mjTask);
		SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		mjTask.setImageUrl(FileUtil.imageUrlToBase64(cacheObject.getImgReturnUrl() + mjTask.getImageUrl()));
		SseEmitterServer.sendMessage(mjTask.getUserId(),mjTask);
	}

}
