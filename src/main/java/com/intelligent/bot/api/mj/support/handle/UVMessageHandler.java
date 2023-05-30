package com.intelligent.bot.api.mj.support.handle;


import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.mj.support.Task;
import com.intelligent.bot.api.mj.support.TaskCondition;
import com.intelligent.bot.enums.mj.Action;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.mj.data.MessageData;
import com.intelligent.bot.service.mj.TaskService;
import com.intelligent.bot.utils.mj.ConvertUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UVMessageHandler implements MessageHandler {
	private final TaskService taskQueueService;

	@Override
	public void onMessageReceived(Message message) {
		MessageData messageData = ConvertUtils.matchUVContent(message.getContentRaw());
		if (messageData == null) {
			return;
		}
		Set<TaskStatus> set = new HashSet<>();
		set.add(TaskStatus.SUBMITTED);
		set.add(TaskStatus.IN_PROGRESS);
		TaskCondition condition = new TaskCondition()
				.setKey(message.getReferencedMessage().getId() + "-" + messageData.getAction())
				.setStatusSet(set);
		Task task = this.taskQueueService.findTask(condition)
				.max(Comparator.comparing(Task::getSubmitTime))
				.orElse(null);
		if (task == null) {
			return;
		}
		task.setMessageId(message.getId());
		finishTask(task, message);
		task.awake();
	}

	@Override
	public void onMessageUpdate(Message message) {
		String content = message.getContentRaw();
		MessageData data = ConvertUtils.matchImagineContent(content);
		if (data == null) {
			data = ConvertUtils.matchUVContent(content);
		}
		if (data == null) {
			return;
		}
		String relatedTaskId = ConvertUtils.findTaskIdByFinalPrompt(data.getPrompt());
		if (CharSequenceUtil.isBlank(relatedTaskId)) {
			return;
		}
		Set<TaskStatus> set = new HashSet<>();
		set.add(TaskStatus.SUBMITTED);
		set.add(TaskStatus.IN_PROGRESS);
		Set<Action> actionSet = new HashSet<>();
		actionSet.add(Action.UPSCALE);
		actionSet.add(Action.VARIATION);
		TaskCondition condition = new TaskCondition()
				.setActionSet(actionSet)
				.setRelatedTaskId(relatedTaskId)
				.setStatusSet(set);
		Task task = this.taskQueueService.findTask(condition)
				.max(Comparator.comparing(Task::getSubmitTime))
				.orElse(null);
		if (task == null) {
			return;
		}
		task.setStatus(TaskStatus.IN_PROGRESS);
		task.awake();
	}

}
