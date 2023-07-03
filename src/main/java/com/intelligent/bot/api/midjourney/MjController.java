package com.intelligent.bot.api.midjourney;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.req.mj.*;
import com.intelligent.bot.service.baidu.BaiDuService;
import com.intelligent.bot.service.mj.TaskService;
import com.intelligent.bot.service.mj.TaskStoreService;
import com.intelligent.bot.service.sys.CheckService;
import com.intelligent.bot.utils.mj.BannedPromptUtils;
import com.intelligent.bot.utils.mj.MimeTypeUtils;
import com.intelligent.bot.utils.sys.IDUtil;
import com.intelligent.bot.utils.sys.JwtUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/mj")
@Log4j2
public class MjController {

	@Resource
	BaiDuService baiDuService;
	@Resource
	TaskStoreService taskStoreService;
	@Resource
	CheckService checkService;
	@Resource
	TaskService taskService;

	@PostMapping(value = "/submit",name = "提交Imagine或UV任务")
	public B<Task> submit(@RequestBody SubmitReq req) {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		if(null == sysConfig.getIsOpenMj() || sysConfig.getIsOpenMj() == 0){
			throw new E("暂未开启Mj");
		}
		String prompt = req.getPrompt();
		if (CharSequenceUtil.isBlank(prompt)) {
			throw new E("prompt不能为空");
		}
		prompt = prompt.trim();
		Task task = newTask();
		task.setAction(TaskAction.IMAGINE);
		task.setPrompt(prompt);
		String promptEn;
		int paramStart = prompt.indexOf(" --");
		if (paramStart > 0) {
			promptEn = this.baiDuService.translateToEnglish(prompt.substring(0, paramStart)).trim() + prompt.substring(paramStart);
		} else {
			promptEn = this.baiDuService.translateToEnglish(prompt).trim();
		}
		if (CharSequenceUtil.isBlank(promptEn)) {
			promptEn = prompt;
		}
		if (BannedPromptUtils.isBanned(promptEn)) {
			throw new E("可能包含敏感词");
		}
		checkService.checkUser(JwtUtil.getUserId(),CommonConst.MJ_NUMBER);
		task.setPromptEn(promptEn
				+(!StringUtils.isEmpty(req.getNo()) ? " --no "+(this.baiDuService.translateToEnglish(req.getNo())) : "")
				+(!StringUtils.isEmpty(req.getVersion()) ? " " +req.getVersion() : "")
				+(!StringUtils.isEmpty(req.getStyle()) ? " " +req.getStyle() : "")
				+(!StringUtils.isEmpty(req.getAr()) ? " " +req.getAr() : "")
				+(!StringUtils.isEmpty(req.getQ()) ? " " +req.getQ() : "")
				+(!StringUtils.isEmpty(req.getStylize()) ? " " + req.getStylize() : "")
				+(!StringUtils.isEmpty(req.getChaos()) ? " " +req.getChaos() : ""));
		this.taskService.submitImagine(task,req.getImgList());
		return B.okBuild(task);
	}

	@PostMapping(value = "/submit/uv",name = "提交选中放大或变换任务")
	public B<Task> submitUV(@RequestBody UVSubmitReq req) {
		if (null == req.getId()) {
			throw new E("id 不能为空");
		}
		if(!Arrays.asList(TaskAction.UPSCALE, TaskAction.VARIATION, TaskAction.REROLL).contains(req.getTaskAction())){
			throw new E("action参数错误");
		}
		Task task = taskStoreService.get(req.getId());
		if(null == task){
			throw new E(" 任务不存在");
		}
		if(task.getStatus() != TaskStatus.SUCCESS){
			throw new E("关联任务状态错误");
		}
		if (!Arrays.asList(TaskAction.IMAGINE, TaskAction.VARIATION).contains(task.getAction())) {
			throw new E("关联任务不允许执行变化");
		}
		String description = "/up " + task.getId();
		if (TaskAction.REROLL.equals(req.getTaskAction())) {
			description += " R";
		} else {
			description += " " + req.getTaskAction().name().charAt(0) + req.getIndex();
		}
		TaskCondition condition = new TaskCondition().setDescription(description);
		Task existTask = this.taskStoreService.findOne(condition);
		if(null != existTask){
			throw new E(" 任务已存在 ");
		}
		Task mjTask = newTask();
		mjTask.setAction(req.getTaskAction());
		mjTask.setPrompt(task.getPrompt());
		mjTask.setPromptEn(task.getPromptEn());
		mjTask.setRelatedTaskId(task.getId());
		mjTask.setDescription(description);
		mjTask.setIndex(req.getIndex());
		mjTask.setFinalPrompt(task.getFinalPrompt());
		if (TaskAction.UPSCALE.equals(req.getTaskAction())) {
			this.taskService.submitUpscale(mjTask, task.getMessageId(), task.getMessageHash(), req.getIndex(),task.getFlags());
		} else if (TaskAction.VARIATION.equals(req.getTaskAction())) {
			this.taskService.submitVariation(mjTask, task.getMessageId(), task.getMessageHash(), req.getIndex(),task.getFlags());
		} else {
			throw new E("不支持的操作");
		}
		return B.okBuild(mjTask);
	}

	@PostMapping(value = "/describe",name = "提交Describe图生文任务")
	public B<Task> describe(@RequestBody DescribeReq req) {
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
		Task task = newTask();
		task.setAction(TaskAction.DESCRIBE);
		String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
		task.setDescription("/describe " + taskFileName);
		this.taskService.submitDescribe(task, dataUrl);
		return B.okBuild(task);
	}

	@PostMapping(value = "/blend",name = "提交Blend任务")
	public B<Task> blend(@RequestBody SubmitBlendReq req) {
		List<String> base64Array = req.getBase64Array();
		if (base64Array == null || base64Array.size() < 2 || base64Array.size() > 5) {
			throw new E("base64List参数错误");
		}
		IDataUrlSerializer serializer = new DataUrlSerializer();
		List<DataUrl> dataUrlList = new ArrayList<>();
		try {
			for (String base64 : base64Array) {
				DataUrl dataUrl = serializer.unserialize(base64);
				dataUrlList.add(dataUrl);
			}
		} catch (MalformedURLException e) {
			throw new E("base64格式错误");
		}
		Task task = newTask();
		task.setAction(TaskAction.BLEND);
		task.setDescription("/blend " + task.getId() + " " + dataUrlList.size());
		this.taskService.submitBlend(task, dataUrlList,req.getDimensions());
		return B.okBuild(task);
	}



	@PostMapping("getTask")
	public B<Task> getTask(@RequestBody TaskReq req) {
		return B.okBuild(taskStoreService.get(req.getId()));
	}
//	@PostMapping("callBack")
	public void callBack(@RequestBody MjCallBack mjTask) throws Exception {
//		log.info("mj开始回调,回调内容：{}", mjTask);
//		if(mjTask.getStatus() == TaskStatus.SUCCESS){
//			MjTask targetTask = new MjTask();
//			String localImgUrl = FileUtil.base64ToImage((mjTask.getImageUrl()));
//			targetTask.setImageUrl(localImgUrl);
//			targetTask.setId(mjTask.getId());
//			targetTask.setStatus(TaskStatus.SUCCESS);
//			mjTask.setImageUrl(targetTask.getImageUrl());
//			asyncService.updateMjTask(targetTask);
//		}
//		SseEmitterServer.sendMessage(mjTask.getUserId(),mjTask);
	}

	private Task newTask() {
		SysConfig sysConfig = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		Task task = new Task();
		task.setId(IDUtil.getNextId());
		task.setSubmitTime(System.currentTimeMillis());
		task.setNotifyHook(sysConfig.getApiUrl() + CommonConst.MJ_CALL_BACK_URL);
		task.setUserId(JwtUtil.getUserId());
		task.setSubType(1);
		return task;
	}

}
