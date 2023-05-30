package com.intelligent.bot.service.mj;



import com.intelligent.bot.api.mj.support.Task;

import java.util.List;

public interface TaskStoreService {

	void saveTask(Task task);

	void deleteTask(String id);

	Task getTask(String id);

	List<Task> listTask();

}
