package com.intelligent.bot.service.mj;


import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.model.MjTask;

public interface TaskStoreService {

	void saveTask(MjTask task);

	void deleteTask(Long id);

	MjTask getTask(Long id);

	MjTask findOne(TaskCondition taskCondition);


}
