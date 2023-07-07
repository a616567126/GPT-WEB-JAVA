package com.intelligent.bot.service.mj.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.CheckedUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.req.mj.MjCallBack;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.IMjTaskService;
import com.intelligent.bot.utils.sys.FileUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;

@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final ThreadPoolTaskExecutor executor;
	private final TimedCache<Long, Object> taskLocks = CacheUtil.newTimedCache(Duration.ofHours(1).toMillis());

	public NotifyServiceImpl() {
		this.executor = new ThreadPoolTaskExecutor();
		this.executor.setCorePoolSize(CommonConst.NOTIFY_POOL_SIZE);
		this.executor.setThreadNamePrefix("TaskNotify-");
		this.executor.initialize();
	}

	@Resource
	IMjTaskService mjTaskService;

	@Resource
	AsyncService asyncService;

	@Override
	public void notifyTaskChange(Task task) {
		String notifyHook = task.getNotifyHook();
		if (CharSequenceUtil.isBlank(notifyHook)) {
			return;
		}
		// 获取线程所需的参数，避免在线程中获取
		Long taskId = task.getId();
		TaskStatus taskStatus = task.getStatus();
		Object taskLock = this.taskLocks.get(taskId, (CheckedUtil.Func0Rt<Object>) Object::new);
		log.debug("创建任务变更线程, 任务ID: {}, status: {}", taskId, taskStatus);
		try {
			String paramsStr = OBJECT_MAPPER.writeValueAsString(task);
			this.executor.execute(() -> {
				synchronized (taskLock) {
					try {
						log.debug("开始推送任务变更, 任务ID: {}, status: {}", taskId, taskStatus);
						postJson(notifyHook, paramsStr);
					} catch (Exception e) {
						log.warn("推送任务变更失败, 任务ID: {}, 描述: {}", taskId, e.getMessage());
					}
				}
			});
		} catch (JsonProcessingException e) {
			log.warn("创建任务ID: {}, status: {}, 描述: {}", taskId, taskStatus, e.getMessage());
		}
	}

	private void postJson(String notifyHook, String paramsJson) throws WxErrorException, IOException {
		MjCallBack mjTask = JSONObject.parseObject(paramsJson, MjCallBack.class);
		log.info("mj开始回调,回调内容：{}", mjTask);
		if(mjTask.getStatus().equals(TaskStatus.SUCCESS) && null != mjTask.getImageUrl()){
			String fileLocalPath = FileUtil.base64ToImage(FileUtil.imageUrlToBase64(mjTask.getImageUrl()), mjTask.getAction() == TaskAction.IMAGINE ? String.valueOf(mjTask.getId()) : null);
			if(mjTask.getSubType() == 2){
				mjTask.setImageUrl(fileLocalPath);
				asyncService.sendMjWxMessage(BeanUtil.copyProperties(mjTask,Task.class));
			}else {
				SseEmitterServer.sendMessage(mjTask.getUserId(),mjTask);
			}
			Task task = new Task();
			task.setId(mjTask.getId());
			task.setImageUrl(fileLocalPath);
			task.setStatus(TaskStatus.SUCCESS);
			mjTaskService.updateById(task);
		}else {
			if(mjTask.getSubType() == 1 && null != mjTask.getImageUrl()){
				mjTask.setImageUrl(FileUtil.imageUrlToBase64(mjTask.getImageUrl()));
				SseEmitterServer.sendMessage(mjTask.getUserId(),mjTask);
			}
		}
	}
}
