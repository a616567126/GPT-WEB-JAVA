package com.intelligent.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.DiscordHelper;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.User;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.IUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
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
	@Resource
	IUserService userService;
	@Resource
	WxMpService wxMpService;

	@Resource
	protected DiscordHelper discordHelper;

	@Override
	public void handle(MessageType messageType, DataObject message) throws WxErrorException {
		Optional<DataArray> embedsOptional = message.optArray("embeds");
		if (!embedsOptional.isPresent() || embedsOptional.get().isEmpty()) {
			return;
		}
		DataObject embed = embedsOptional.get().getObject(0);
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
		if (CharSequenceUtil.contains(description, "this job will start")) {
			// mj队列中, 不认为是异常
			return;
		}
		if (CharSequenceUtil.contains(description, "verify you're human")) {
			String reason = "需要人工验证，请联系管理员";
			this.taskQueueHelper.findRunningTask(new TaskCondition()).forEach(task -> {
				task.fail(reason);
				task.awake();
			});
			return;
		}
		Task targetTask = null;
		if (CharSequenceUtil.startWith(footerText, "/imagine ")) {
			String finalPrompt = CharSequenceUtil.subAfter(footerText, "/imagine ", false);
			if (CharSequenceUtil.contains(finalPrompt, "https://")) {
				// 有可能为blend操作
				String taskId = this.discordHelper.findTaskIdWithCdnUrl(finalPrompt.split(" ")[0]);
				if (taskId != null) {
					targetTask = this.taskQueueHelper.getRunningTask(Long.valueOf(taskId));
				}
			}
			if (targetTask == null) {
				targetTask = this.taskQueueHelper.findRunningTask(t ->
								t.getAction() == TaskAction.IMAGINE && finalPrompt.startsWith(t.getPromptEn()))
						.findFirst().orElse(null);
			}
		} else if (CharSequenceUtil.startWith(footerText, "/describe ")) {
			String imageUrl = CharSequenceUtil.subAfter(footerText, "/describe ", false);
			String taskId = this.discordHelper.findTaskIdWithCdnUrl(imageUrl);
			targetTask = this.taskQueueHelper.getRunningTask(Long.valueOf(taskId));
		}
		if (targetTask == null) {
			return;
		}
		String reason;
		if (CharSequenceUtil.contains(description, "against our community standards")) {
			reason = "可能包含违规信息";
		} else {
			reason = description;
		}
		if(targetTask.getSubType() == 2){
			User user = userService.getById(targetTask.getUserId());
			WxMpKefuMessage wxMpKefuMessage = WxMpKefuMessage.TEXT().toUser(user.getFromUserName()).content(reason).build();
			wxMpService.getKefuService().sendKefuMessage(wxMpKefuMessage);
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
