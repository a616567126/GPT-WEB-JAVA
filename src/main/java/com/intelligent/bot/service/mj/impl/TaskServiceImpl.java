package com.intelligent.bot.service.mj.impl;

import com.intelligent.bot.api.midjourney.loadbalancer.DiscordInstance;
import com.intelligent.bot.api.midjourney.loadbalancer.DiscordLoadBalancer;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.enums.mj.BlendDimensions;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.model.Task;
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
	private DiscordLoadBalancer discordLoadBalancer;


	@Override
	public B<Void>  submitImagine(Task task, List<String> imgList) {
		DiscordInstance instance = this.discordLoadBalancer.chooseInstance();
		if (instance == null) {
			throw new E("无可用的账号实例");
		}
		task.setDiscordInstanceId(instance.getInstanceId());
		return instance.submitTask(task, () -> {
			if(null != imgList){
				StringBuilder image = new StringBuilder();
				for (String img : imgList) {
					image.append(img).append(" ");
				}
				task.setPromptEn(image + task.getPromptEn());
				task.setPrompt(image + task.getPrompt());
			}
			return instance.imagine(task.getPromptEn(),task.getNonce());
		});
	}

	@Override
	public B<Void> submitUpscale(Task task, String targetMessageId, String targetMessageHash, int index, int messageFlags) {
		String instanceId = task.getDiscordInstanceId();
		DiscordInstance discordInstance = this.discordLoadBalancer.getDiscordInstance(instanceId);
		if (discordInstance == null || !discordInstance.isAlive()) {
			throw new E("账号不可用: " + instanceId);
		}
		return discordInstance.submitTask(task, () -> discordInstance.upscale(targetMessageId, index, targetMessageHash,messageFlags,task.getNonce()));
	}

	@Override
	public B<Void> submitVariation(Task task, String targetMessageId, String targetMessageHash, int index, int messageFlags) {
		String instanceId = task.getDiscordInstanceId();
		DiscordInstance discordInstance = this.discordLoadBalancer.getDiscordInstance(instanceId);
		if (discordInstance == null || !discordInstance.isAlive()) {
			throw new E("账号不可用: " + instanceId);
		}
		return discordInstance.submitTask(task, () -> discordInstance.variation(targetMessageId, index, targetMessageHash,messageFlags,task.getNonce()));
	}

	@Override
	public B<Void> submitReroll(Task task, String targetMessageId, String targetMessageHash, int messageFlags) {
		String instanceId = task.getDiscordInstanceId();
		DiscordInstance discordInstance = this.discordLoadBalancer.getDiscordInstance(instanceId);
		if (discordInstance == null || !discordInstance.isAlive()) {
			throw new E("账号不可用: " + instanceId);
		}
		return discordInstance.submitTask(task, () -> discordInstance.reroll(targetMessageId, targetMessageHash, messageFlags,task.getNonce()));
	}

	@Override
	public B<Void> submitDescribe(Task task, DataUrl dataUrl) {
		DiscordInstance discordInstance = this.discordLoadBalancer.chooseInstance();
		if (discordInstance == null) {
			throw new E("无可用的账号实例");
		}
		task.setDiscordInstanceId(discordInstance.getInstanceId());
		return discordInstance.submitTask(task, () -> {
			String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
			B<String> uploadResult = discordInstance.upload(taskFileName, dataUrl);
			if (uploadResult.getStatus() != ResultEnum.SUCCESS.getCode()) {
				throw new E(uploadResult.getMessage());
			}
			String finalFileName = uploadResult.getData();
			return discordInstance.describe(finalFileName,task.getNonce());
		});
	}

	@Override
	public B<Void> submitBlend(Task task, List<DataUrl> dataUrls,BlendDimensions dimensions) {
		DiscordInstance discordInstance = this.discordLoadBalancer.chooseInstance();
		if (discordInstance == null) {
			throw new E("无可用的账号实例");
		}
		task.setDiscordInstanceId(discordInstance.getInstanceId());
		return discordInstance.submitTask(task, () -> {
			List<String> finalFileNames = new ArrayList<>();
			for (DataUrl dataUrl : dataUrls) {
				String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
				B<String> uploadResult = discordInstance.upload(taskFileName, dataUrl);
				if (uploadResult.getStatus() != ResultEnum.SUCCESS.getCode()) {
					throw new E(uploadResult.getMessage());
				}
				finalFileNames.add(uploadResult.getData());
			}
			return discordInstance.blend(finalFileNames, dimensions,task.getNonce());
		});
	}
}