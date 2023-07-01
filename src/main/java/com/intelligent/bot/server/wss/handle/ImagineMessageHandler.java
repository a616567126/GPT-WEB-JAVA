package com.intelligent.bot.server.wss.handle;

import com.intelligent.bot.api.midjourney.support.DiscordHelper;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.mj.data.ContentParseData;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * imagine消息处理.
 * 开始(create): **[4619231091196848] cat** - <@1012983546824114217> (Waiting to start)
 * 进度(update): **[4619231091196848] cat** - <@1012983546824114217> (0%) (relaxed)
 * 完成(create): **[4619231091196848] cat** - <@1012983546824114217> (relaxed)
 */
@Slf4j
@Component
public class ImagineMessageHandler extends MessageHandler {
	private static final String CONTENT_REGEX = "\\*\\*(.*?)\\*\\* - <@\\d+> \\((.*?)\\)";

	@Resource
	protected DiscordHelper discordHelper;

	@Override
	public void handle(MessageType messageType, DataObject message) throws IOException {
		String content = getMessageContent(message);
		ContentParseData parseData = parse(content);
		if (parseData == null) {
			return;
		}
		String realPrompt = this.discordHelper.getRealPrompt(parseData.getPrompt());
		if (MessageType.CREATE == messageType) {
			if ("Waiting to start".equals(parseData.getStatus())) {
				// 开始
				TaskCondition condition = new TaskCondition()
						.setActionSet(Collections.singletonList(TaskAction.IMAGINE))
						.setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
				Task task = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt))
						.findFirst().orElse(null);
				if (task == null) {
					return;
				}
				task.setProgressMessageId(message.getString("id"));
				task.setFinalPrompt(parseData.getPrompt());
				task.setStatus(TaskStatus.IN_PROGRESS);
				task.awake();
			} else {
				// 完成
				TaskCondition condition = new TaskCondition()
						.setActionSet(Collections.singletonList(TaskAction.IMAGINE))
						.setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
				Task task = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt))
						.findFirst().orElse(null);
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
					.setActionSet(Collections.singletonList(TaskAction.IMAGINE))
					.setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
			Task task = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt))
					.findFirst().orElse(null);
			if (task == null) {
				return;
			}
			task.setProgressMessageId(message.getString("id"));
			task.setFinalPrompt(parseData.getPrompt());
			task.setStatus(TaskStatus.IN_PROGRESS);
			task.setProgress(parseData.getStatus());
			task.setImageUrl(getImageUrl(task,message));
			task.awake();
		}
	}

	@Override
	public void handle(MessageType messageType, Message message) throws IOException {
		String content = message.getContentRaw();
		ContentParseData parseData = parse(content);
		if (parseData == null) {
			return;
		}
		String realPrompt = this.discordHelper.getRealPrompt(parseData.getPrompt());
		if (MessageType.CREATE == messageType) {
			if ("Waiting to start".equals(parseData.getStatus())) {
				// 开始
				TaskCondition condition = new TaskCondition()
						.setActionSet(Collections.singletonList(TaskAction.IMAGINE))
						.setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
				Task task = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt))
						.findFirst().orElse(null);
				if (task == null) {
					return;
				}
				task.setProgressMessageId(message.getId());
				task.setFinalPrompt(parseData.getPrompt());
				task.setStatus(TaskStatus.IN_PROGRESS);
				task.awake();
			} else {
				// 完成
				TaskCondition condition = new TaskCondition()
						.setActionSet(Collections.singletonList(TaskAction.IMAGINE))
						.setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
				Task task = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt))
						.findFirst().orElse(null);
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
					.setActionSet(Collections.singletonList(TaskAction.IMAGINE))
					.setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
			Task task = this.taskQueueHelper.findRunningTask(taskPredicate(condition, realPrompt))
					.findFirst().orElse(null);
			if (task == null) {
				return;
			}
			task.setProgressMessageId(message.getId());
			task.setFinalPrompt(parseData.getPrompt());
			task.setStatus(TaskStatus.IN_PROGRESS);
			task.setProgress(parseData.getStatus());
			task.setImageUrl(getImageUrl(task,message));
			task.awake();
		}
	}

	private Predicate<Task> taskPredicate(TaskCondition condition, String prompt) {
		return condition.and(t -> prompt.startsWith(t.getPromptEn()));
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
