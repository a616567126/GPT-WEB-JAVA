package com.intelligent.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.TaskQueueHelper;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.utils.sys.FileUtil;
import me.chanjar.weixin.common.error.WxErrorException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import javax.annotation.Resource;
import java.io.IOException;

public abstract class MessageHandler {
	@Resource
	protected TaskQueueHelper taskQueueHelper;


	public abstract void handle(MessageType messageType, DataObject message) throws IOException, WxErrorException;

	public abstract void handle(MessageType messageType, Message message) throws IOException;

	protected String getMessageContent(DataObject message) {
		return message.hasKey("content") ? message.getString("content") : "";
	}


	protected void finishTask(Task task, DataObject message) throws IOException {
		task.setMessageId( message.getString("id"));
		task.setFlags(message.getInt("flags", 0));
		DataArray attachments = message.getArray("attachments");
		if (!attachments.isEmpty()) {
			String imageUrl = attachments.getObject(0).getString("url");
			task.setImageUrl(imageUrl);
			task.setImageUrl(replaceCdnUrl(task));
			task.setMessageHash(getMessageHash(imageUrl));
			task.success();
		} else {
			task.fail("关联图片不存在");
		}
	}

	protected void finishTask(Task task, Message message) throws IOException {
		task.setMessageId( message.getId());
		task.setFlags((int) message.getFlagsRaw());
		if (!message.getAttachments().isEmpty()) {
			String imageUrl = message.getAttachments().get(0).getUrl();
			task.setImageUrl(imageUrl);
			task.setImageUrl(replaceCdnUrl(task));
			task.setMessageHash(getMessageHash(imageUrl));
			task.success();
		} else {
			task.fail("关联图片不存在");
		}
	}

	protected String getMessageHash(String imageUrl) {
		int hashStartIndex = imageUrl.lastIndexOf("_");
		return CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true);
	}

	protected String getImageUrl(Task task,Message message) throws IOException {
		if (!message.getAttachments().isEmpty()) {
			String imageUrl = message.getAttachments().get(0).getUrl();
			task.setImageUrl(imageUrl);
			return replaceCdnUrl(task);
		}
		return null;
	}

	protected String getImageUrl(Task task,DataObject message) throws IOException {
		DataArray attachments = message.getArray("attachments");
		if (!attachments.isEmpty()) {
			String imageUrl = attachments.getObject(0).getString("url");
			task.setImageUrl(imageUrl);
			return replaceCdnUrl(task);
		}
		return null;
	}

	protected String replaceCdnUrl(Task task) throws IOException {
		if (CharSequenceUtil.isBlank(task.getImageUrl())) {
			return "";
		}
		return FileUtil.base64ToImage(FileUtil.imageUrlToBase64(task.getImageUrl()),task.getAction() == TaskAction.IMAGINE ? String.valueOf(task.getId()) : null);
	}

}
