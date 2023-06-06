package com.intelligent.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.TaskQueueHelper;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.model.MjTask;
import com.intelligent.bot.utils.sys.FileUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import javax.annotation.Resource;
import java.io.IOException;

public abstract class MessageHandler {
	@Resource
	protected TaskQueueHelper taskQueueHelper;

	public abstract void handle(MessageType messageType, DataObject message) throws IOException;

	public abstract void handle(MessageType messageType, Message message) throws IOException;

	protected void updateTaskImageUrl(MjTask task, DataObject message) throws IOException {
		DataArray attachments = message.getArray("attachments");
		if (!attachments.isEmpty()) {
			String imageUrl = attachments.getObject(0).getString("url");
			task.setImageUrl(imageUrl);
			task.setImageUrl(replaceCdnUrl(task));
		}
	}

	protected void finishTask(MjTask task, DataObject message) throws IOException {
		task.setMessageId(message.getString("id"));
		DataArray attachments = message.getArray("attachments");
		if (!attachments.isEmpty()) {
			String imageUrl = attachments.getObject(0).getString("url");
			task.setImageUrl(imageUrl);
			task.setImageUrl(replaceCdnUrl(task));
			int hashStartIndex = imageUrl.lastIndexOf("_");
			task.setMessageHash(CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true));
			task.success();
		} else {
			task.fail("关联图片不存在");
		}
	}

	protected void updateTaskImageUrl(MjTask task, Message message) throws IOException {
		if (!message.getAttachments().isEmpty()) {
			String imageUrl = message.getAttachments().get(0).getUrl();
			task.setImageUrl(imageUrl);
			task.setImageUrl(replaceCdnUrl(task));
		}
	}

	protected void finishTask(MjTask task, Message message) throws IOException {
		task.setMessageId(message.getId());
		if (!message.getAttachments().isEmpty()) {
			String imageUrl = message.getAttachments().get(0).getUrl();
			task.setImageUrl(imageUrl);
			task.setImageUrl(replaceCdnUrl(task));
			int hashStartIndex = imageUrl.lastIndexOf("_");
			task.setMessageHash(CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true));
			task.success();
		} else {
			task.fail("关联图片不存在");
		}
	}

	protected String replaceCdnUrl(MjTask task) throws IOException {
		if (CharSequenceUtil.isBlank(task.getImageUrl())) {
			return "";
		}
		return FileUtil.base64ToImage(FileUtil.imageUrlToBase64(task.getImageUrl()),task.getTaskAction() == TaskAction.IMAGINE ? String.valueOf(task.getId()) : null);
	}

}
