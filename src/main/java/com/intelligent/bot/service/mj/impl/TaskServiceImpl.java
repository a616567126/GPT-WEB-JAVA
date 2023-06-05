package com.intelligent.bot.service.mj.impl;

import com.intelligent.bot.api.midjourney.support.TaskQueueHelper;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.model.MjTask;
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
	public B<Void>  submitImagine(MjTask task) {
		return this.taskQueueHelper.submitTask(task, () -> this.discordService.imagine(task.getFinalPrompt()));
	}

	@Override
	public B<Void> submitUpscale(MjTask task, String targetMessageId, String targetMessageHash, int index) {
		return this.taskQueueHelper.submitTask(task, () -> this.discordService.upscale(targetMessageId, index, targetMessageHash));
	}

	@Override
	public B<Void> submitVariation(MjTask task, String targetMessageId, String targetMessageHash, int index) {
		return this.taskQueueHelper.submitTask(task, () -> this.discordService.variation(targetMessageId, index, targetMessageHash));
	}

	@Override
	public B<Void> submitDescribe(MjTask task, DataUrl dataUrl) {
		return this.taskQueueHelper.submitTask(task, () -> {
			String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
			B<String> uploadResult = this.discordService.upload(taskFileName, dataUrl);
			if (uploadResult.getStatus() != ResultEnum.SUCCESS.getCode()) {
				throw new E(uploadResult.getMessage());
			}
			String finalFileName = uploadResult.getData();
			return this.discordService.describe(finalFileName);
		});
	}

	@Override
	public B<Void> submitBlend(MjTask task, List<DataUrl> dataUrls) {
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
			return this.discordService.blend(finalFileNames);
		});
	}
}