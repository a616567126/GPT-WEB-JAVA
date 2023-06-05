package com.intelligent.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.MjTask;
import com.intelligent.bot.model.mj.data.UVContentParseData;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * variation消息处理. todo: 待兼容blend
 * 开始(create): Making variations for image #1 with prompt **[0152010266005012] cat** - <@1012983546824114217> (Waiting to start)
 * 进度(update): **[0152010266005012] cat** - Variations by <@1012983546824114217> (0%) (relaxed)
 * 完成(create): **[0152010266005012] cat** - Variations by <@1012983546824114217> (relaxed)
 */
@Slf4j
@Component
public class VariationMessageHandler extends MessageHandler {
	private static final String START_CONTENT_REGEX = "Making variations for image #(\\d) with prompt \\*\\*\\[(\\d+)\\] (.*?)\\*\\* - <@\\d+> \\((.*?)\\)";
	private static final String CONTENT_REGEX = "\\*\\*\\[(\\d+)\\] (.*?)\\*\\* - Variations by <@\\d+> \\((.*?)\\)";

	@Override
	public void handle(MessageType messageType, DataObject message) throws IOException {
		String content = message.getString("content");
		if (MessageType.CREATE.equals(messageType)) {
			UVContentParseData start = parseStart(content);
			if (start != null) {
				// 开始
				TaskCondition condition = new TaskCondition()
						.setRelatedTaskId(start.getTaskId())
						.setActionSet(Collections.singletonList(TaskAction.VARIATION))
						.setStatusSet(Collections.singletonList(TaskStatus.SUBMITTED));
				MjTask task = this.taskQueueHelper.findRunningTask(condition)
						.filter(t -> CharSequenceUtil.endWith(t.getDescription(), "V" + start.getIndex()))
						.min(Comparator.comparing(MjTask::getSubmitTime))
						.orElse(null);
				if (task == null) {
					return;
				}
				task.setMessageId(message.getString("id"));
				task.setStatus(TaskStatus.IN_PROGRESS);
				task.awake();
				return;
			}
			UVContentParseData end = parse(content);
			if (end == null) {
				return;
			}
			TaskCondition condition = new TaskCondition()
					.setRelatedTaskId(end.getTaskId())
					.setActionSet(Collections.singletonList(TaskAction.VARIATION))
					.setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
			MjTask task = this.taskQueueHelper.findRunningTask(condition)
					.max(Comparator.comparing(MjTask::getProgress))
					.orElse(null);
			if (task == null) {
				return;
			}
			finishTask(task, message);
			task.awake();
		} else if (MessageType.UPDATE == messageType) {
			UVContentParseData parseData = parse(content);
			if (parseData == null || CharSequenceUtil.equalsAny(parseData.getStatus(), "relaxed", "fast")) {
				return;
			}
			TaskCondition condition = new TaskCondition()
					.setMessageId(message.getString("id"))
					.setActionSet(Collections.singletonList(TaskAction.VARIATION))
					.setStatusSet(Collections.singletonList(TaskStatus.IN_PROGRESS));
			MjTask task = this.taskQueueHelper.findRunningTask(condition)
					.findFirst().orElse(null);
			if (task == null) {
				return;
			}
			task.setProgress(parseData.getStatus());
			updateTaskImageUrl(task, message);
			task.awake();
		}
	}

	/**
	 * bot-wss模式，取不到执行进度; todo: 同个任务不同变换对应不上.
	 *
	 * @param messageType messageType
	 * @param message     message
	 */
	@Override
	public void handle(MessageType messageType, Message message) throws IOException {
		String content = message.getContentRaw();
		if (MessageType.CREATE.equals(messageType)) {
			UVContentParseData parseData = parse(content);
			if (parseData == null) {
				return;
			}
			TaskCondition condition = new TaskCondition()
					.setRelatedTaskId(parseData.getTaskId())
					.setActionSet(Collections.singletonList(TaskAction.VARIATION))
					.setStatusSet(Arrays.asList(TaskStatus.SUBMITTED, TaskStatus.IN_PROGRESS));
			MjTask task = this.taskQueueHelper.findRunningTask(condition)
					.min(Comparator.comparing(MjTask::getSubmitTime))
					.orElse(null);
			if (task == null) {
				return;
			}
			finishTask(task, message);
			task.awake();
		}
	}

	private UVContentParseData parseStart(String content) {
		Matcher matcher = Pattern.compile(START_CONTENT_REGEX).matcher(content);
		if (!matcher.find()) {
			return null;
		}
		UVContentParseData parseData = new UVContentParseData();
		parseData.setIndex(Integer.parseInt(matcher.group(1)));
		parseData.setTaskId(Long.valueOf(matcher.group(2)));
		parseData.setPrompt(matcher.group(3));
		parseData.setStatus(matcher.group(4));
		return parseData;
	}

	private UVContentParseData parse(String content) {
		Matcher matcher = Pattern.compile(CONTENT_REGEX).matcher(content);
		if (!matcher.find()) {
			return null;
		}
		UVContentParseData parseData = new UVContentParseData();
		parseData.setTaskId(Long.valueOf(matcher.group(1)));
		parseData.setPrompt(matcher.group(2));
		parseData.setStatus(matcher.group(3));
		return parseData;
	}
}
