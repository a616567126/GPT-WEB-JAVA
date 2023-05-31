package com.intelligent.bot.api.midjourney.support.handle;


import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.Task;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.service.mj.TaskService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DescribeMessageHandler implements MessageHandler {
	private final TaskService taskQueueService;

	@Override
	public void onMessageReceived(Message message) {
	}

	@Override
	public void onMessageUpdate(Message message) {
		List<MessageEmbed> embeds = message.getEmbeds();
		if (embeds.isEmpty()) {
			return;
		}
		String prompt = embeds.get(0).getDescription();
		String imageUrl = Objects.requireNonNull(embeds.get(0).getImage()).getUrl();
		assert imageUrl != null;
		int hashStartIndex = imageUrl.lastIndexOf("/");
		String taskId = CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true);
		Task task = this.taskQueueService.getTask(taskId);
		if (task == null) {
			return;
		}
		task.setMessageId(message.getId());
		task.setPrompt(prompt);
		task.setPromptEn(prompt);
		task.setImageUrl(imageUrl);
		task.setFinishTime(System.currentTimeMillis());
		task.setStatus(TaskStatus.SUCCESS);
		task.awake();
	}

}
