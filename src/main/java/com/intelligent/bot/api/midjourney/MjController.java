package com.intelligent.bot.api.midjourney;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.api.midjourney.support.BannedPromptHelper;
import com.intelligent.bot.api.midjourney.support.Task;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.Action;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.mj.*;
import com.intelligent.bot.model.req.sys.MessageLogSave;
import com.intelligent.bot.model.res.mj.GetTaskRes;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.baidu.BaiDuService;
import com.intelligent.bot.service.mj.DiscordMessageService;
import com.intelligent.bot.service.mj.TaskService;
import com.intelligent.bot.service.mj.TaskStoreService;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.CheckService;
import com.intelligent.bot.service.sys.IMessageLogService;
import com.intelligent.bot.utils.mj.ConvertUtils;
import com.intelligent.bot.utils.mj.MimeTypeUtils;
import com.intelligent.bot.utils.sys.DateUtil;
import com.intelligent.bot.utils.sys.FileUtil;
import com.intelligent.bot.utils.sys.JwtUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.util.Collections;

@RestController
@RequestMapping("/mj")
@Log4j2
public class MjController {

	@Resource
	BaiDuService baiDuService;
	@Resource
	TaskStoreService taskStoreService;
	@Resource
	IMessageLogService messageLogService;
	@Resource
	AsyncService asyncService;
	@Resource
	CheckService checkService;
	@Resource
	BannedPromptHelper bannedPromptHelper;
	@Resource
	TaskService taskService;
	@Resource
	DiscordMessageService discordMessageService;

	@PostMapping(value = "/submit",name = "提交Imagine或UV任务")
	public B<Task> submit(@RequestBody SubmitReq req) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		if(null == sysConfig.getIsOpenMj() || sysConfig.getIsOpenMj() == 0){
			throw new E("暂未开启Mj");
		}
		if (req.getAction() == null) {
			throw new E("校验错误");
		}
		if ((req.getAction() == Action.UPSCALE || req.getAction() == Action.VARIATION)
				&& (req.getIndex() < 1 || req.getIndex() > 4)) {
			throw new E("校验错误");
		}
		if(req.getAction() == Action.IMAGINE){
			if(CharSequenceUtil.isBlank(req.getPrompt()) || req.getPrompt().contains("nsfw") || !baiDuService.textToExamine(req.getPrompt())){
				throw new E("生成内容不合规");
			}
		}
		Task task = new Task();
		task.setNotifyHook(sysConfig.getApiUrl() + CommonConst.MJ_CALL_BACK_URL);
		task.setSubmitTime(System.currentTimeMillis());
		task.setAction(req.getAction());
		Long logId = checkService.checkUser(MessageLog.builder()
				.useNumber(CommonConst.MJ_NUMBER)
				.sendType(SendType.MJ.getType())
				.useValue(req.getPrompt())
				.userId(JwtUtil.getUserId()).build(),req.getLogId());
		task.setId(RandomUtil.randomNumbers(16));
		task.setState(String.valueOf(logId));
		if (Action.IMAGINE.equals(req.getAction())) {
			String prompt = req.getPrompt();
			task.setKey(task.getId());
			task.setPrompt(prompt);
			String promptEn;
			int paramStart = prompt.indexOf(" --");
			if (paramStart > 0) {
				promptEn = this.baiDuService.translateToEnglish(prompt.substring(0, paramStart)).trim() + prompt.substring(paramStart);
			} else {
				promptEn = this.baiDuService.translateToEnglish(prompt).trim();
			}
			if (this.bannedPromptHelper.isBanned(promptEn)) {
				asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.FS_NUMBER);
				throw new E("生成内容可能包含敏感词");
			}
			task.setPromptEn(promptEn);
			task.setFinalPrompt("[" + task.getId() + "] " + promptEn);
			if(null != req.getImgList()){
				StringBuilder image = new StringBuilder();
				for (String img : req.getImgList()) {
					image.append(img).append(" ");
				}
				task.setFinalPrompt("[" + task.getId() + "] " + image + promptEn);
			}
			task.setDescription("/imagine " + req.getPrompt());
			this.taskService.submitImagine(task);
			return B.okBuild(task);
		}
		if (CharSequenceUtil.isBlank(req.getTaskId())) {
			asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.FS_NUMBER);
			throw new E("校验错误");
		}
		Task targetTask = this.taskStoreService.getTask(req.getTaskId());
		if (targetTask == null) {
			asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.FS_NUMBER);
			throw new E("任务不存在或已失效");
		}
		if (!TaskStatus.SUCCESS.equals(targetTask.getStatus())) {
			asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.FS_NUMBER);
			throw new E("关联任务状态错误");
		}
		task.setPrompt(targetTask.getPrompt());
		task.setPromptEn(targetTask.getPromptEn());
		task.setFinalPrompt(targetTask.getFinalPrompt());
		task.setRelatedTaskId(ConvertUtils.findTaskIdByFinalPrompt(targetTask.getFinalPrompt()));
		task.setKey(targetTask.getMessageId() + "-" + req.getAction());
		if (Action.UPSCALE.equals(req.getAction())) {
			task.setDescription("/up " + req.getTaskId() + " U" + req.getIndex());
			this.taskService.submitUpscale(task, targetTask.getMessageId(), targetTask.getMessageHash(), req.getIndex());
			return B.okBuild(task);
		} else if (Action.VARIATION.equals(req.getAction())) {
			task.setDescription("/up " + req.getTaskId() + " V" + req.getIndex());
			this.taskService.submitVariation(task, targetTask.getMessageId(), targetTask.getMessageHash(), req.getIndex());
			return B.okBuild(task);
		} else {
			asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.FS_NUMBER);
			throw new E("不支持的操作");
		}
	}

	@PostMapping(value = "/submit/uv",name = "提交选中放大或变换任务")
	public B<Task> submitUV(@RequestBody UVSubmitReq req) {
		return submit(BeanUtil.copyProperties(req,SubmitReq.class));
	}

	@PostMapping(value = "/describe",name = "提交Describe图生文任务")
	public B<String> describe(@RequestBody DescribeReq req) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		if (CharSequenceUtil.isBlank(req.getBase64())) {
			throw new E("校验错误");
		}
		IDataUrlSerializer serializer = new DataUrlSerializer();
		DataUrl dataUrl;
		try {
			dataUrl = serializer.unserialize(req.getBase64());
		} catch (MalformedURLException e) {
			throw new E("base64格式错误");
		}
		Task task = new Task();
		task.setSubmitTime(System.currentTimeMillis());
		task.setId(RandomUtil.randomNumbers(16));
		String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
		task.setState(req.getState());
		task.setAction(Action.DESCRIBE);
		task.setDescription("/describe " + taskFileName);
		task.setKey(taskFileName);
		task.setNotifyHook(sysConfig.getApiUrl() + CommonConst.MJ_CALL_BACK_URL);
		return this.taskService.submitDescribe(task, dataUrl);
	}

	@PostMapping("getTask")
	public B<GetTaskRes> getTask(@RequestBody TaskReq req) {
		return B.okBuild(discordMessageService.getMjMessages(req.getTaskId()));
	}
	@PostMapping("callBack")
	public void callBack(@RequestBody MjCallBack mjCallBack) throws Exception {
		log.info("mj开始回调,回调内容：{}", mjCallBack);
		SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		if(null != mjCallBack.getImageUrl()){
			String localImgUrl = FileUtil.base64ToImage(FileUtil.imageUrlToBase64(mjCallBack.getImageUrl()));
			mjCallBack.setImageUrl(cacheObject.getImgReturnUrl() + localImgUrl);
			MessageLog messageLog = messageLogService.getById(Long.valueOf(mjCallBack.getState()));
			SseEmitterServer.sendMessage(Long.valueOf(mjCallBack.getState()), JSONObject.toJSONString(mjCallBack));
			Task task = taskStoreService.getTask(mjCallBack.getId());
			JSONObject prompt = new JSONObject();
			prompt.put("prompt",task.getPrompt());
			prompt.put("promptEn",task.getPromptEn());
			prompt.put("taskId",task.getId());
			MessageLogSave messageLogSave = MessageLogSave.builder()
					.prompt(JSONObject.toJSONString(prompt))
					.type(SendType.MJ.getRemark())
					.startTime(DateUtil.timestamp2LocalDateTime(mjCallBack.getSubmitTime()))
					.imgList(Collections.singletonList(localImgUrl)).build();
			asyncService.updateLog(messageLog.getId(),messageLogSave);
		}
	}


}
