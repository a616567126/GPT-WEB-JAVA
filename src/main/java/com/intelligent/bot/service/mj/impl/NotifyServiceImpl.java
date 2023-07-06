package com.intelligent.bot.service.mj.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.exceptions.CheckedUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.User;
import com.intelligent.bot.model.req.mj.MjCallBack;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.sys.IMjTaskService;
import com.intelligent.bot.service.sys.IUserService;
import com.intelligent.bot.utils.sys.FileUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
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
	IUserService userService;
	@Resource
	WxMpService wxMpService;
	@Resource
	IMjTaskService mjTaskService;

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
		SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		if(mjTask.getStatus().equals(TaskStatus.SUCCESS) && null != mjTask.getImageUrl()){
			String fileLocalPath = FileUtil.base64ToImage(FileUtil.imageUrlToBase64(mjTask.getImageUrl()), mjTask.getAction() == TaskAction.IMAGINE ? String.valueOf(mjTask.getId()) : null);
			Task task = new Task();
			task.setId(mjTask.getId());
			task.setImageUrl(fileLocalPath);
			task.setStatus(TaskStatus.SUCCESS);
			mjTaskService.updateById(task);
			if(mjTask.getSubType() == 2){
				User user = userService.getById(mjTask.getUserId());
				String content;
				if(mjTask.getAction().equals(TaskAction.UPSCALE)){
					content = "\uD83C\uDFA8绘图完成\n"+
							"\uD83E\uDD73本次消耗次数："+CommonConst.MJ_NUMBER;
				}else {
					content = "\uD83C\uDFA8绘图完成\n" +
							"\uD83E\uDD73本次消耗次数："+CommonConst.MJ_NUMBER+"\n"+
							"\uD83D\uDCAC咒语："+mjTask.getPrompt()+"\n"+
							"\uD83D\uDDEF译文："+mjTask.getPromptEn()+"\n"+
							"\uD83D\uDD0D\uFE0E放大命令：\n"+
							"<a href=\"weixin://bizmsgmenu?msgmenucontent=/U-1-"+mjTask.getId()+"&msgmenuid=1\">放大1</a>\t"+
							"<a href=\"weixin://bizmsgmenu?msgmenucontent=/U-2-"+mjTask.getId()+"&msgmenuid=1\">放大2</a>\t"+
							"<a href=\"weixin://bizmsgmenu?msgmenucontent=/U-3-"+mjTask.getId()+"&msgmenuid=1\">放大3</a>\t"+
							"<a href=\"weixin://bizmsgmenu?msgmenucontent=/U-4-"+mjTask.getId()+"&msgmenuid=1\">放大4</a>\n"+
							"\uD83D\uDCAB变换命令：\n"+
							"<a href=\"weixin://bizmsgmenu?msgmenucontent=/V-1-"+mjTask.getId()+"&msgmenuid=1\">变换1</a>\t"+
							"<a href=\"weixin://bizmsgmenu?msgmenucontent=/V-2-"+mjTask.getId()+"&msgmenuid=1\">变换2</a>\t"+
							"<a href=\"weixin://bizmsgmenu?msgmenucontent=/V-3-"+mjTask.getId()+"&msgmenuid=1\">变换3</a>\t"+
							"<a href=\"weixin://bizmsgmenu?msgmenucontent=/V-4-"+mjTask.getId()+"&msgmenuid=1\">变换4</a>";
				}
				String fileUrl = cacheObject.getImgUploadUrl() + fileLocalPath;
				File file = new File(fileUrl);
				WxMediaUploadResult wxMediaUploadResult = wxMpService.getMaterialService().mediaUpload(WxConsts.MediaFileType.IMAGE, file);
				WxMpKefuMessage message = WxMpKefuMessage.IMAGE().toUser(user.getFromUserName()).mediaId(wxMediaUploadResult.getMediaId()).build();
				wxMpService.getKefuService().sendKefuMessage(message);
				message=WxMpKefuMessage.TEXT().toUser(user.getFromUserName()).content(content).build();
				wxMpService.getKefuService().sendKefuMessage(message);
			}else {
//				mjTask.setImageUrl(FileUtil.imageUrlToBase64(cacheObject.getImgReturnUrl() + fileLocalPath));
				SseEmitterServer.sendMessage(mjTask.getUserId(),mjTask);
			}
		}else {
			if(mjTask.getSubType() == 1 && null != mjTask.getImageUrl()){
				mjTask.setImageUrl(FileUtil.imageUrlToBase64(mjTask.getImageUrl()));
				SseEmitterServer.sendMessage(mjTask.getUserId(),mjTask);
			}
		}
	}
}
