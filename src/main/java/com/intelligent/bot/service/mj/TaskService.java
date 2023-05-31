package com.intelligent.bot.service.mj;


import com.intelligent.bot.api.midjourney.support.Task;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.base.result.B;
import eu.maxschuster.dataurl.DataUrl;

import java.util.stream.Stream;

public interface TaskService {

	Task getTask(String id);

	Stream<Task> findTask(TaskCondition condition);

	B<String> submitImagine(Task task);

	B<String> submitUpscale(Task task, String targetMessageId, String targetMessageHash, int index);

	B<String> submitVariation(Task task, String targetMessageId, String targetMessageHash, int index);

	B<String> submitDescribe(Task task, DataUrl dataUrl);
}