package com.intelligent.bot.api.midjourney.support;

import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.model.MjTask;
import com.intelligent.bot.service.mj.NotifyService;
import com.intelligent.bot.service.mj.TaskStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Slf4j
@Component
public class TaskQueueHelper {
	@Resource
	private TaskStoreService taskStoreService;
	@Resource
	private NotifyService notifyService;

	private final ThreadPoolTaskExecutor taskExecutor;
	private final ThreadPoolTaskExecutor waitFutureExecutor;
	private final List<MjTask> runningTasks;
	private final Map<Long, Future<?>> taskFutureMap = Collections.synchronizedMap(new HashMap<>());

	public TaskQueueHelper() {
		this.runningTasks = Collections.synchronizedList(new ArrayList<>(CommonConst.MJ_TASK_QUEUE_CORE_SIZE * 2));
		this.taskExecutor = new ThreadPoolTaskExecutor();
		this.taskExecutor.setCorePoolSize(CommonConst.MJ_TASK_QUEUE_CORE_SIZE);
		this.taskExecutor.setMaxPoolSize(CommonConst.MJ_TASK_QUEUE_CORE_SIZE);
		this.taskExecutor.setQueueCapacity(CommonConst.MJ_TASK_QUEUE_QUEUE_SIZE);
		this.taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		this.taskExecutor.setThreadNamePrefix("TaskQueue-");
		this.taskExecutor.initialize();

		this.waitFutureExecutor = new ThreadPoolTaskExecutor();
		this.waitFutureExecutor.setCorePoolSize(CommonConst.MJ_TASK_QUEUE_CORE_SIZE);
		this.waitFutureExecutor.setThreadNamePrefix("WaitFuture-");
		this.waitFutureExecutor.initialize();
	}

	public Set<Long> getQueueTaskIds() {
		return this.taskFutureMap.keySet();
	}

	public MjTask getRunningTask(Long id) {
		if (null == id) {
			return null;
		}
		return this.runningTasks.stream().filter(t -> id.equals(t.getId())).findFirst().orElse(null);
	}

	public Stream<MjTask> findRunningTask(TaskCondition condition) {
		return this.runningTasks.stream().filter(condition);
	}

	public B<Void> submitTask(MjTask task, Callable<B<Void>> discordSubmit) {
		this.taskStoreService.saveTask(task);
		int size;
		try {
			size = this.taskExecutor.getThreadPoolExecutor().getQueue().size();
			Future<?> future = this.taskExecutor.submit(() -> executeTask(task, discordSubmit));
			this.taskFutureMap.put(task.getId(), future);
		} catch (RejectedExecutionException e) {
			this.taskStoreService.deleteTask(task.getId());
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

	private void executeTask(MjTask task, Callable<B<Void>> discordSubmit) {
		this.runningTasks.add(task);
		try {
			B<Void> result = discordSubmit.call();
			if (result.getStatus() != ResultEnum.SUCCESS.getCode()) {
				task.fail(result.getMessage());
				changeStatusAndNotify(task, TaskStatus.FAILURE);
				return;
			}
			task.start();
			changeStatusAndNotify(task, TaskStatus.SUBMITTED);
			waitTaskFuture(task);
			do {
				task.sleep();
				changeStatusAndNotify(task, task.getStatus());
			} while (task.getStatus() == TaskStatus.IN_PROGRESS || task.getStatus() == TaskStatus.SUCCESS);
			log.debug("task finished, id: {}, status: {}", task.getId(), task.getStatus());
		} catch (InterruptedException e) {
			log.debug("task timeout, id: {}", task.getId());
			if(task.getStatus()  != TaskStatus.SUCCESS){
				task.fail("任务超时");
				changeStatusAndNotify(task, TaskStatus.FAILURE);
				Thread.currentThread().interrupt();
			}
		} catch (Exception e) {
			log.error("task execute error", e);
			if(task.getStatus()  != TaskStatus.SUCCESS){
				task.fail("执行错误，系统异常");
				changeStatusAndNotify(task, TaskStatus.FAILURE);
			}
		} finally {
			this.runningTasks.remove(task);
			this.taskFutureMap.remove(task.getId());
		}
	}

	private void waitTaskFuture(MjTask task) {
		Future<?> future = this.taskFutureMap.get(task.getId());
		if (future == null) {
			task.fail("执行错误，系统异常");
			changeStatusAndNotify(task, TaskStatus.FAILURE);
			return;
		}
		this.waitFutureExecutor.execute(() -> {
			try {
				future.get(CommonConst.TIMEOUT_MINUTES, TimeUnit.MINUTES);
			} catch (TimeoutException e) {
				future.cancel(true);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		});
	}

	private void changeStatusAndNotify(MjTask task, TaskStatus status) {
		task.setStatus(status);
		this.taskStoreService.saveTask(task);
		this.notifyService.notifyTaskChange(task);
	}
}