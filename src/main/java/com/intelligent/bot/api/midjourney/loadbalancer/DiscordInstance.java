package com.intelligent.bot.api.midjourney.loadbalancer;


import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.mj.doman.DiscordAccount;
import com.intelligent.bot.service.mj.DiscordService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface DiscordInstance extends DiscordService {

	String getInstanceId();

	DiscordAccount account();

	boolean isAlive();

	void startWss() throws Exception;

	List<Task> getRunningTasks();

	void exitTask(Task task);

	Map<Long, Future<?>> getRunningFutures();

	B<Void> submitTask(Task task, Callable<B<Void>> discordSubmit);

}
