package com.intelligent.bot.api.midjourney.loadbalancer;


import com.intelligent.bot.base.result.B;
import com.intelligent.bot.enums.mj.BlendDimensions;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.mj.doman.DiscordAccount;
import com.intelligent.bot.server.wss.WebSocketStarter;
import com.intelligent.bot.server.wss.user.UserWebSocketStarter;
import com.intelligent.bot.service.mj.DiscordService;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.mj.TaskStoreService;
import com.intelligent.bot.service.mj.impl.DiscordServiceImpl;
import eu.maxschuster.dataurl.DataUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
public class DiscordInstanceImpl implements DiscordInstance {
	private final DiscordAccount account;
	private final WebSocketStarter socketStarter;
	private final DiscordService service;
	private final TaskStoreService taskStoreService;
	private final NotifyService notifyService;

	private final ThreadPoolTaskExecutor taskExecutor;
	private final List<Task> runningTasks;
	private final Map<Long, Future<?>> taskFutureMap = Collections.synchronizedMap(new HashMap<>());

	public DiscordInstanceImpl(DiscordAccount account, UserWebSocketStarter socketStarter,
							   RestTemplate restTemplate,TaskStoreService taskStoreService,
							   NotifyService notifyService,Map<String, String> paramsMap) {
		this.account = account;
		this.socketStarter = socketStarter;
		this.taskStoreService = taskStoreService;
		this.notifyService = notifyService;
		this.service = new DiscordServiceImpl(account,restTemplate ,paramsMap);
		this.runningTasks = new CopyOnWriteArrayList<>();
		this.taskExecutor = new ThreadPoolTaskExecutor();
		this.taskExecutor.setCorePoolSize(account.getCoreSize());
		this.taskExecutor.setMaxPoolSize(account.getCoreSize());
		this.taskExecutor.setQueueCapacity(account.getQueueSize());
		this.taskExecutor.setThreadNamePrefix("TaskQueue-" + account.getDisplay() + "-");
		this.taskExecutor.initialize();
	}

	@Override
	public String getInstanceId() {
		return this.account.getChannelId();
	}

	@Override
	public DiscordAccount account() {
		return this.account;
	}

	@Override
	public boolean isAlive() {
		return this.account.getState() == 1;
	}

	@Override
	public void startWss() throws Exception {
		this.socketStarter.setTrying(true);
		this.socketStarter.start();
	}

	@Override
	public List<Task> getRunningTasks() {
		return this.runningTasks;
	}

	@Override
	public void exitTask(Task task) {
		try {
			Future<?> future = this.taskFutureMap.get(task.getId());
			if (future != null) {
				future.cancel(true);
			}
			saveAndNotify(task);
		} finally {
			this.runningTasks.remove(task);
			this.taskFutureMap.remove(task.getId());
		}
	}

	@Override
	public Map<Long, Future<?>> getRunningFutures() {
		return this.taskFutureMap;
	}

	@Override
	public synchronized B<Void> submitTask(Task task, Callable<B<Void>> discordSubmit) {
		this.taskStoreService.save(task);
		int currentWaitNumbers;
		try {
			currentWaitNumbers = this.taskExecutor.getThreadPoolExecutor().getQueue().size();
			Future<?> future = this.taskExecutor.submit(() -> executeTask(task, discordSubmit));
			this.taskFutureMap.put(task.getId(), future);
		} catch (RejectedExecutionException e) {
			this.taskStoreService.delete(task.getId());
			return B.finalBuild("队列已满，请稍后尝试");
		} catch (Exception e) {
			log.error("submit task error", e);
			return  B.finalBuild("提交失败，系统异常");
		}
		if (currentWaitNumbers == 0) {
			return  B.okBuild();
		} else {
			return  B.finalBuild ("排队中，前面还有" + currentWaitNumbers + "个任务");
		}
	}

	private void executeTask(Task task, Callable<B<Void>> discordSubmit) {
		this.runningTasks.add(task);
		try {
			task.start();
			B<Void> result = discordSubmit.call();
			if (result.getStatus() != ResultEnum.SUCCESS.getCode()) {
				task.fail(result.getMessage());
				saveAndNotify(task);
				return;
			}
			saveAndNotify(task);
			do {
				task.sleep();
				saveAndNotify(task);
			} while (task.getStatus() == TaskStatus.IN_PROGRESS);
			log.debug("task finished, id: {}, status: {}", task.getId(), task.getStatus());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			log.error("task execute error", e);
			task.fail("执行错误，系统异常");
			saveAndNotify(task);
		} finally {
			this.runningTasks.remove(task);
			this.taskFutureMap.remove(task.getId());
		}
	}

	private void saveAndNotify(Task task) {
		this.taskStoreService.save(task);
		this.notifyService.notifyTaskChange(task);
	}

	@Override
	public B<Void> imagine(String prompt, String nonce) {
		return this.service.imagine(prompt, nonce);
	}

	@Override
	public B<Void> upscale(String messageId, int index, String messageHash, int messageFlags, String nonce) {
		return this.service.upscale(messageId, index, messageHash, messageFlags, nonce);
	}

	@Override
	public B<Void> variation(String messageId, int index, String messageHash, int messageFlags, String nonce) {
		return this.service.variation(messageId, index, messageHash, messageFlags, nonce);
	}

	@Override
	public B<Void> reroll(String messageId, String messageHash, int messageFlags, String nonce) {
		return this.service.reroll(messageId, messageHash, messageFlags, nonce);
	}

	@Override
	public B<Void> describe(String finalFileName, String nonce) {
		return this.service.describe(finalFileName, nonce);
	}

	@Override
	public B<Void> blend(List<String> finalFileNames, BlendDimensions dimensions, String nonce) {
		return this.service.blend(finalFileNames, dimensions, nonce);
	}

	@Override
	public B<String> upload(String fileName, DataUrl dataUrl) {
		return this.service.upload(fileName, dataUrl);
	}

	@Override
	public B<String> sendImageMessage(String content, String finalFileName) {
		return this.service.sendImageMessage(content, finalFileName);
	}

}
