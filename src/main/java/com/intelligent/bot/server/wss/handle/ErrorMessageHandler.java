package com.intelligent.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.model.MjTask;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.utils.mj.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@Component
public class ErrorMessageHandler extends MessageHandler {

	@Resource
	AsyncService asyncService;
	@Override
	public void handle(MessageType messageType, DataObject message) {
		DataArray embeds = message.getArray("embeds");
		if (embeds.isEmpty()) {
			return;
		}
		DataObject embed = embeds.getObject(0);
		String title = embed.getString("title", null);
		if (CharSequenceUtil.isBlank(title) || CharSequenceUtil.startWith(title, "Your info - ")) {
			// 排除正常信息.
			return;
		}
		String description = embed.getString("description", null);
		String footerText = "";
		Optional<DataObject> footer = embed.optObject("footer");
		if (footer.isPresent()) {
			footerText = footer.get().getString("text", "");
		}
		log.warn("检测到可能异常的信息: {}\n{}\nfooter: {}", title, description, footerText);
		MjTask targetTask = null;
		if (CharSequenceUtil.startWith(footerText, "/imagine ")) {
			String finalPrompt = CharSequenceUtil.subAfter(footerText, "/imagine ", false);
			String taskId = ConvertUtils.findTaskIdByFinalPrompt(finalPrompt);
			targetTask = this.taskQueueHelper.getRunningTask(Long.valueOf(taskId));
		} else if (CharSequenceUtil.startWith(footerText, "/describe ")) {
			String imageUrl = CharSequenceUtil.subAfter(footerText, "/describe ", false);
			int hashStartIndex = imageUrl.lastIndexOf("/");
			String taskId = CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true);
			targetTask = this.taskQueueHelper.getRunningTask(Long.valueOf(taskId));
		}
		if (targetTask == null) {
			return;
		}
		String reason;
		if (CharSequenceUtil.contains(description, "against our community standards")) {
			reason = "可能包含违规信息";
			asyncService.updateRemainingTimes(targetTask.getUserId(), CommonConst.MJ_NUMBER);
		} else if (CharSequenceUtil.contains(description, "verify you're human")) {
			reason = "需要人工验证，请联系管理员";
			asyncService.updateRemainingTimes(targetTask.getUserId(), CommonConst.MJ_NUMBER);
		} else {
			reason = description;
		}
		targetTask.fail(reason);
		targetTask.awake();
		asyncService.updateMjTask(targetTask);
	}

	@Override
	public void handle(MessageType messageType, Message message) {
		// bot-wss 获取不到错误
	}

}
