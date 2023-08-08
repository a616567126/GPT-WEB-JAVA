package com.intelligent.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.DiscordHelper;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.api.midjourney.support.TaskQueueHelper;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.model.Task;
import me.chanjar.weixin.common.error.WxErrorException;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import javax.annotation.Resource;
import java.util.Comparator;

public abstract class MessageHandler {
	@Resource
	protected TaskQueueHelper taskQueueHelper;

	@Resource
	protected DiscordHelper discordHelper;

	public abstract void handle(MessageType messageType, DataObject message) throws WxErrorException;

	protected String getMessageContent(DataObject message) {
		return message.hasKey("content") ? message.getString("content") : "";
	}

	protected String getMessageNonce(DataObject message) {
		return message.hasKey("nonce") ? message.getString("nonce") : "";
	}

	protected void findAndFinishImageTask(TaskCondition condition, String finalPrompt, DataObject message) {
		Task task = this.taskQueueHelper.findRunningTask(condition)
				.max(Comparator.comparing(Task::getProgress))
				.orElse(null);
		if (task == null) {
			return;
		}
		task.setImageUrl(getImageUrl(message));
		task.setFinalPrompt(finalPrompt);
		finishTask(task, message);
		task.awake();
	}

	protected void finishTask(Task task, DataObject message) {
		task.setMessageId( message.getString("id"));
		task.setMessageHash(getMessageHash(task.getImageUrl()));
		task.setFlags( message.getInt("flags", 0));
		task.success();
	}

	protected boolean hasImage(DataObject message) {
		DataArray attachments = message.optArray("attachments").orElse(DataArray.empty());
		return !attachments.isEmpty();
	}

	protected String getImageUrl(DataObject message) {
		DataArray attachments = message.getArray("attachments");
		if (!attachments.isEmpty()) {
			String imageUrl = attachments.getObject(0).getString("url");
			return replaceCdnUrl(imageUrl);
		}
		return null;
	}

	protected String replaceCdnUrl(String imageUrl) {
		if (CharSequenceUtil.isBlank(imageUrl)) {
			return "";
		}
//		return FileUtil.base64ToImage(FileUtil.imageUrlToBase64(task.getImageUrl()),task.getAction() == TaskAction.IMAGINE ? String.valueOf(task.getId()) : null);
		return imageUrl;
	}

	protected String getMessageHash(String imageUrl) {
		int hashStartIndex = imageUrl.lastIndexOf("_");
		return CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true);
	}
}
