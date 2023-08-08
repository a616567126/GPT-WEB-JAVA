package com.intelligent.bot.service.mj.impl;

import com.intelligent.bot.api.midjourney.support.TaskQueueHelper;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.enums.mj.BlendDimensions;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.service.mj.DiscordService;
import com.intelligent.bot.service.mj.TaskService;
import com.intelligent.bot.utils.mj.MimeTypeUtils;
import eu.maxschuster.dataurl.DataUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
	@Resource
	private DiscordService discordService;
	@Resource
	TaskQueueHelper taskQueueHelper;


	@Override
	public B<Void>  submitImagine(Task task, List<String> imgList) {
		return this.taskQueueHelper.submitTask(task, () -> {
			if(null != imgList){
				StringBuilder image = new StringBuilder();
				for (String img : imgList) {
					image.append(img).append(" ");
				}
				task.setPromptEn(image + task.getPromptEn());
				task.setPrompt(image + task.getPrompt());
			}
			return this.discordService.imagine(task.getPromptEn(),task.getNonce());
		});
	}

	@Override
	public B<Void> submitUpscale(Task task, String targetMessageId, String targetMessageHash, int index, int messageFlags) {
		return this.taskQueueHelper.submitTask(task, () -> this.discordService.upscale(targetMessageId, index, targetMessageHash,messageFlags,task.getNonce()));
	}

	@Override
	public B<Void> submitVariation(Task task, String targetMessageId, String targetMessageHash, int index, int messageFlags) {
		return this.taskQueueHelper.submitTask(task, () -> this.discordService.variation(targetMessageId, index, targetMessageHash,messageFlags,task.getNonce()));
	}

	@Override
	public B<Void> submitReroll(Task task, String targetMessageId, String targetMessageHash, int messageFlags) {
		return null;
	}

	@Override
	public B<Void> submitDescribe(Task task, DataUrl dataUrl) {
		return this.taskQueueHelper.submitTask(task, () -> {
			String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
			B<String> uploadResult = this.discordService.upload(taskFileName, dataUrl);
			if (uploadResult.getStatus() != ResultEnum.SUCCESS.getCode()) {
				throw new E(uploadResult.getMessage());
			}
			String finalFileName = uploadResult.getData();
			return this.discordService.describe(finalFileName,task.getNonce());
		});
	}

	@Override
	public B<Void> submitBlend(Task task, List<DataUrl> dataUrls,BlendDimensions dimensions) {
		return this.taskQueueHelper.submitTask(task, () -> {
			List<String> finalFileNames = new ArrayList<>();
			for (DataUrl dataUrl : dataUrls) {
				String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
				B<String> uploadResult = this.discordService.upload(taskFileName, dataUrl);
				if (uploadResult.getStatus() != ResultEnum.SUCCESS.getCode()) {
					throw new E(uploadResult.getMessage());
				}
				finalFileNames.add(uploadResult.getData());
			}
			return this.discordService.blend(finalFileNames, dimensions,task.getNonce());
		});
	}
}