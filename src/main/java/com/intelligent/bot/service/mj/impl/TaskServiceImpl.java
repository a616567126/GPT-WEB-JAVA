package com.intelligent.bot.service.mj.impl;

import com.intelligent.bot.api.midjourney.support.Task;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.service.mj.DiscordService;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.mj.TaskService;
import com.intelligent.bot.service.mj.TaskStoreService;
import com.intelligent.bot.utils.mj.MimeTypeUtils;
import eu.maxschuster.dataurl.DataUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
	@Resource
	private TaskStoreService taskStoreService;
	@Resource
	private DiscordService discordService;
	@Resource
	private NotifyService notifyService;

	private final ThreadPoolTaskExecutor taskExecutor;
	private final List<Task> runningTasks;

	public TaskServiceImpl() {
		this.runningTasks = Collections.synchronizedList(new ArrayList<>(CommonConst.MJ_TASK_QUEUE_CORE_SIZE * 2));
		this.taskExecutor = new ThreadPoolTaskExecutor();
		this.taskExecutor.setCorePoolSize(CommonConst.MJ_TASK_QUEUE_CORE_SIZE);
		this.taskExecutor.setMaxPoolSize(CommonConst.MJ_TASK_QUEUE_CORE_SIZE);
		this.taskExecutor.setQueueCapacity(CommonConst.MJ_TASK_QUEUE_QUEUE_SIZE);
		this.taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		this.taskExecutor.setThreadNamePrefix("TaskQueue-");
		this.taskExecutor.initialize();
	}

	@Override
	public Task getTask(String id) {
		return this.runningTasks.stream().filter(t -> id.equals(t.getId())).findFirst().orElse(null);
	}

	@Override
	public Stream<Task> findTask(TaskCondition condition) {
		return this.runningTasks.stream().filter(condition);
	}

	@Override
	public B<String> submitImagine(Task task) {
		return submitTask(task, () -> {
			B<Void> result = this.discordService.imagine(task.getFinalPrompt());
			checkAndWait(task, result);
		});
	}

	@Override
	public B<String> submitUpscale(Task task, String targetMessageId, String targetMessageHash, int index) {
		return submitTask(task, () -> {
			B<Void> result = this.discordService.upscale(targetMessageId, index, targetMessageHash);
			checkAndWait(task, result);
		});
	}

	@Override
	public B<String> submitVariation(Task task, String targetMessageId, String targetMessageHash, int index) {
		return submitTask(task, () -> {
			B<Void> result = this.discordService.variation(targetMessageId, index, targetMessageHash);
			checkAndWait(task, result);
		});
	}

	@Override
	public B<String> submitDescribe(Task task, DataUrl dataUrl) {
		return submitTask(task, () -> {
			String taskFileName = task.getId() + "." + MimeTypeUtils.guessFileSuffix(dataUrl.getMimeType());
			B<String> uploadResult = this.discordService.upload(taskFileName, dataUrl);
			if (uploadResult.getStatus() != ResultEnum.SUCCESS.getCode()) {
				task.setFinishTime(System.currentTimeMillis());
				task.setFailReason(uploadResult.getData());
				changeStatusAndNotify(task, TaskStatus.FAILURE);
				return;
			}
			String finalFileName = uploadResult.getData();
			B<Void> result = this.discordService.describe(finalFileName);
			checkAndWait(task, result);
		});
	}

	private B<String> submitTask(Task task, Runnable runnable) {
		this.taskStoreService.saveTask(task);
		int size;
		try {
			size = this.taskExecutor.getThreadPoolExecutor().getQueue().size();
			this.taskExecutor.execute(() -> {
				task.setStartTime(System.currentTimeMillis());
				this.runningTasks.add(task);
				try {
					this.taskStoreService.saveTask(task);
					runnable.run();
				} finally {
					this.runningTasks.remove(task);
				}
			});
		} catch (RejectedExecutionException e) {
			this.taskStoreService.deleteTask(task.getId());
			throw new E("队列已满，请稍后尝试");
		}
		if (size == 0) {
			return B.okBuild(task.getId());
		} else {
			throw new E("排队中，前面还有" + size + "个任务");
		}
	}

	private void checkAndWait(Task task, B<Void> result) {
		if (result.getStatus() != ResultEnum.SUCCESS.getCode()) {
			task.setFinishTime(System.currentTimeMillis());
			task.setFailReason(String.valueOf(result.getData()));
			changeStatusAndNotify(task, TaskStatus.FAILURE);
			return;
		}
		changeStatusAndNotify(task, TaskStatus.SUBMITTED);
		do {
			try {
				task.sleep();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			changeStatusAndNotify(task, task.getStatus());
		} while (task.getStatus() == TaskStatus.IN_PROGRESS);
		log.debug("task finished, id: {}, status: {}", task.getId(), task.getStatus());
	}

	private void changeStatusAndNotify(Task task, TaskStatus status) {
		task.setStatus(status);
		this.taskStoreService.saveTask(task);
		this.notifyService.notifyTaskChange(task);
	}

}