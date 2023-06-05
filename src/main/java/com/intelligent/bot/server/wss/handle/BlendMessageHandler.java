package com.intelligent.bot.server.wss.handle;


import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.MjTask;
import com.intelligent.bot.model.mj.data.ContentParseData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * blend消息处理.
 * 开始(create): **https://xxx/xxx1/1780749341481612.png https://xxx/xxx2/1780749341481612.png --v 5.1** - <@1012983546824114217> (Waiting to start)
 * 进度(update): **<https://s.mj.run/JWu6jaL1D-8> <https://s.mj.run/QhfnQY-l68o> --v 5.1** - <@1012983546824114217> (0%) (relaxed)
 * 完成(create): **<https://s.mj.run/JWu6jaL1D-8> <https://s.mj.run/QhfnQY-l68o> --v 5.1** - <@1012983546824114217> (relaxed)
 */
@Component
public class BlendMessageHandler extends MessageHandler {
	private static final String CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - <@\\d+> \\((.*?)\\)";

	@Override
	public void handle(MessageType messageType, DataObject message) throws IOException {
		Optional<DataObject> interaction = message.optObject("interaction");
		String content = message.getString("content");
		boolean match = CharSequenceUtil.startWith(content, "**<https://s.mj.run/") || (interaction.isPresent() && "blend".equals(interaction.get().getString("name")));
		if (!match) {
			return;
		}
		ContentParseData parseData = parse(content);
		if (parseData == null) {
			return;
		}
		if (MessageType.CREATE == messageType) {
			if ("Waiting to start".equals(parseData.getStatus())) {
				// 开始
				List<String> urls = CharSequenceUtil.split(parseData.getPrompt(), " ");
				if (urls.isEmpty()) {
					return;
				}
				int hashStartIndex = urls.get(0).lastIndexOf("/");
				String taskId = CharSequenceUtil.subBefore(urls.get(0).substring(hashStartIndex + 1), ".", true);
				TaskCondition condition = new TaskCondition()
						.setId(Long.valueOf(taskId))
						.setActionSet(Collections.singletonList(TaskAction.BLEND))
						.setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
				MjTask task = this.taskQueueHelper.findRunningTask(condition).findFirst().orElse(null);
				if (task == null) {
					return;
				}
				task.setMessageId(message.getString("id"));
				task.setPrompt(parseData.getPrompt());
				task.setPromptEn(parseData.getPrompt());
				task.setStatus(TaskStatus.IN_PROGRESS);
				task.awake();
			} else {
				// 完成
				TaskCondition condition = new TaskCondition()
						.setActionSet(Collections.singletonList(TaskAction.BLEND))
						.setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
				MjTask task = this.taskQueueHelper.findRunningTask(condition)
						.max(Comparator.comparing(MjTask::getProgress))
						.orElse(null);
				if (task == null) {
					return;
				}
				task.setFinalPrompt(parseData.getPrompt());
				finishTask(task, message);
				task.awake();
			}
		} else if (MessageType.UPDATE == messageType) {
			// 进度
			TaskCondition condition = new TaskCondition()
					.setMessageId(message.getString("id"))
					.setActionSet(Collections.singletonList(TaskAction.BLEND))
					.setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
			MjTask task = this.taskQueueHelper.findRunningTask(condition).findFirst().orElse(null);
			if (task == null) {
				return;
			}
			task.setProgress(parseData.getStatus());
			updateTaskImageUrl(task, message);
			task.awake();
		}
	}

	@Override
	public void handle(MessageType messageType, Message message) throws IOException {
		String content = message.getContentRaw();
		boolean match = CharSequenceUtil.startWith(content, "**<https://s.mj.run/") || (message.getInteraction() != null && "blend".equals(message.getInteraction().getName()));
		if (!match) {
			return;
		}
		ContentParseData parseData = parse(content);
		if (parseData == null) {
			return;
		}
		if (MessageType.CREATE == messageType) {
			if ("Waiting to start".equals(parseData.getStatus())) {
				// 开始
				List<String> urls = CharSequenceUtil.split(parseData.getPrompt(), " ");
				if (urls.isEmpty()) {
					return;
				}
				int hashStartIndex = urls.get(0).lastIndexOf("/");
				String taskId = CharSequenceUtil.subBefore(urls.get(0).substring(hashStartIndex + 1), ".", true);
				TaskCondition condition = new TaskCondition()
						.setId(Long.valueOf(taskId))
						.setActionSet(Collections.singletonList(TaskAction.BLEND))
						.setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
				MjTask task = this.taskQueueHelper.findRunningTask(condition).findFirst().orElse(null);
				if (task == null) {
					return;
				}
				task.setMessageId(message.getId());
				task.setPrompt(parseData.getPrompt());
				task.setPromptEn(parseData.getPrompt());
				task.setStatus(TaskStatus.IN_PROGRESS);
				task.awake();
			} else {
				// 完成
				TaskCondition condition = new TaskCondition()
						.setActionSet(Collections.singletonList(TaskAction.BLEND))
						.setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
				MjTask task = this.taskQueueHelper.findRunningTask(condition)
						.max(Comparator.comparing(MjTask::getProgress))
						.orElse(null);
				if (task == null) {
					return;
				}
				task.setFinalPrompt(parseData.getPrompt());
				finishTask(task, message);
				task.awake();
			}
		} else if (MessageType.UPDATE == messageType) {
			// 进度
			TaskCondition condition = new TaskCondition()
					.setMessageId(message.getId())
					.setActionSet(Collections.singletonList(TaskAction.BLEND))
					.setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
			MjTask task = this.taskQueueHelper.findRunningTask(condition).findFirst().orElse(null);
			if (task == null) {
				return;
			}
			task.setProgress(parseData.getStatus());
			updateTaskImageUrl(task, message);
			task.awake();
		}
	}

	private ContentParseData parse(String content) {
		Matcher matcher = Pattern.compile(CONTENT_REGEX).matcher(content);
		if (!matcher.find()) {
			return null;
		}
		ContentParseData parseData = new ContentParseData();
		parseData.setPrompt(matcher.group(1));
		parseData.setStatus(matcher.group(2));
		return parseData;
	}
}
