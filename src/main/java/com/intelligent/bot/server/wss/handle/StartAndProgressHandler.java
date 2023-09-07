package com.intelligent.bot.server.wss.handle;


import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.mj.data.ContentParseData;
import com.intelligent.bot.utils.mj.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class StartAndProgressHandler extends MessageHandler {

	@Override
	public void handle(MessageType messageType, DataObject message) {
		String nonce = getMessageNonce(message);
		String content = getMessageContent(message);
		ContentParseData parseData = ConvertUtils.parseContent(content);
		if (MessageType.CREATE.equals(messageType) && CharSequenceUtil.isNotBlank(nonce)) {
			if (isError(message)) {
				return;
			}
			// 任务开始
			Task task = this.discordLoadBalancer.getRunningTaskByNonce(nonce);
			if (task == null) {
				return;
			}
			task.setProgressMessageId(message.getString("id"));
			// 兼容少数content为空的场景
			if (parseData != null) {
				task.setFinalPrompt(parseData.getPrompt());
			}
			task.setStatus(TaskStatus.IN_PROGRESS);
			task.awake();
		} else if (MessageType.UPDATE.equals(messageType) && parseData != null) {
			// 任务进度
			TaskCondition condition = new TaskCondition().setStatusSet(Arrays.asList(TaskStatus.IN_PROGRESS))
					.setProgressMessageId(message.getString("id"));
			Task task = this.discordLoadBalancer.findRunningTask(condition).findFirst().orElse(null);
			if (task == null) {
				return;
			}
			task.setFinalPrompt( parseData.getPrompt());
			task.setStatus(TaskStatus.IN_PROGRESS);
			task.setProgress(parseData.getStatus());
			String imageUrl = getImageUrl(message);
			task.setImageUrl(imageUrl);
			task.setMessageHash(this.discordHelper.getMessageHash(imageUrl));
			task.awake();
		}
	}

	private boolean isError(DataObject message) {
		Optional<DataArray> embedsOptional = message.optArray("embeds");
		if (!embedsOptional.isPresent() || embedsOptional.get().isEmpty()) {
			return false;
		}
		DataObject embed = embedsOptional.get().getObject(0);
		return embed.getInt("color", 0) == 16711680;
	}

}
