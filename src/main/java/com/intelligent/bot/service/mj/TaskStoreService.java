package com.intelligent.bot.service.mj;


import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.model.Task;

public interface TaskStoreService {

	void save(Task task);

	void delete(Long id);

	Task get(Long id);


	Task findOne(TaskCondition taskCondition);


}
