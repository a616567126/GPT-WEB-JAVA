package com.intelligent.bot.api.midjourney.support;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.mj.TaskStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@Component
public class TaskQueueHelper {
	@Resource
	private TaskStoreService taskStoreService;
	@Resource
	private NotifyService notifyService;

	private final ThreadPoolTaskExecutor taskExecutor;
	private final List<Task> runningTasks;
	private final Map<Long, Future<?>> taskFutureMap = Collections.synchronizedMap(new HashMap<>());

	public TaskQueueHelper() {
		this.taskExecutor = new ThreadPoolTaskExecutor();
		this.runningTasks = new CopyOnWriteArrayList<>();
		this.taskExecutor.setCorePoolSize(CommonConst.MJ_TASK_QUEUE_CORE_SIZE);
		this.taskExecutor.setMaxPoolSize(CommonConst.MJ_TASK_QUEUE_CORE_SIZE);
		this.taskExecutor.setQueueCapacity(CommonConst.MJ_TASK_QUEUE_QUEUE_SIZE);
		this.taskExecutor.setThreadNamePrefix("TaskQueue-");
		this.taskExecutor.initialize();
	}

	public Set<Long> getQueueTaskIds() {
		return this.taskFutureMap.keySet();
	}

	public Task getRunningTask(Long id) {
		if (null == id) {
			return null;
		}
		return this.runningTasks.stream().filter(t -> id.equals(t.getId())).findFirst().orElse(null);
	}

	public Task getRunningTaskByNonce(String nonce) {
		if (CharSequenceUtil.isBlank(nonce)) {
			return null;
		}
		TaskCondition condition = new TaskCondition().setNonce(nonce);
		return findRunningTask(condition).findFirst().orElse(null);
	}

	public Stream<Task> findRunningTask(Predicate<Task> condition) {
		return this.runningTasks.stream().filter(condition);
	}

	public Future<?> getRunningFuture(Long taskId) {
		return this.taskFutureMap.get(taskId);
	}

	public B<Void> submitTask(Task task, Callable<B<Void>> discordSubmit) {
		this.taskStoreService.save(task);
		int size;
		try {
			size = this.taskExecutor.getThreadPoolExecutor().getQueue().size();
			Future<?> future = this.taskExecutor.submit(() -> executeTask(task, discordSubmit));
			this.taskFutureMap.put(task.getId(), future);
		} catch (RejectedExecutionException e) {
			this.taskStoreService.delete(task.getId());
			return B.finalBuild( "队列已满，请稍后尝试");
		} catch (Exception e) {
			log.error("submit task error", e);
			return B.finalBuild( "提交失败，系统异常");
		}
		if (size == 0) {
			return B.okBuild();
		} else {
			return B.finalBuild( "排队中，前面还有" + size + "个任务");
		}
	}

	private void executeTask(Task task, Callable<B<Void>> discordSubmit) {
		this.runningTasks.add(task);
		try {
			task.start();
			B<Void> result = discordSubmit.call();
			if (result.getStatus() !=  ResultEnum.SUCCESS.getCode()) {
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

	public void saveAndNotify(Task task) {
		this.taskStoreService.save(task);
		this.notifyService.notifyTaskChange(task);
	}
}