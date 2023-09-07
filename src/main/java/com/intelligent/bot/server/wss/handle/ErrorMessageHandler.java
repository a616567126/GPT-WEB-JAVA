package com.intelligent.bot.server.wss.handle;

import cn.hutool.core.text.CharSequenceUtil;
import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskStatus;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.User;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.IUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
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


	@Override
	public void handle(MessageType messageType, DataObject message) throws WxErrorException {
		Optional<DataArray> embedsOptional = message.optArray("embeds");
		if (!MessageType.CREATE.equals(messageType) || !embedsOptional.isPresent() || embedsOptional.get().isEmpty()) {
			return;
		}
		DataObject embed = embedsOptional.get().getObject(0);
		String title = embed.getString("title", null);
		String description = embed.getString("description", null);
		String footerText = "";
		Optional<DataObject> footer = embed.optObject("footer");
		if (footer.isPresent()) {
			footerText = footer.get().getString("text", "");
		}
		String channelId = message.getString("channel_id", "");
		int color = embed.getInt("color", 0);
		Task task = null;
		if (color == 16239475) {
			log.warn("{} - MJ警告信息: {}\n{}\nfooter: {}", channelId, title, description, footerText);
		} else if (color == 16711680) {
			log.error("{} - MJ异常信息: {}\n{}\nfooter: {}", channelId, title, description, footerText);
			String nonce = getMessageNonce(message);
			task = this.discordLoadBalancer.getRunningTaskByNonce(nonce);
			if (task != null) {
				task.fail("[" + title + "] " + description);
				task.awake();
			}
		}else if (CharSequenceUtil.contains(title, "Invalid link")) {
			// 兼容 Invalid link! 错误
			log.error("{} - MJ异常信息: {}\n{}\nfooter: {}", channelId, title, description, footerText);
			DataObject messageReference = message.optObject("message_reference").orElse(DataObject.empty());
			String referenceMessageId = messageReference.getString("message_id", "");
			if (CharSequenceUtil.isBlank(referenceMessageId)) {
				return;
			}
			TaskCondition condition = new TaskCondition().setStatusSet(Arrays.asList(TaskStatus.IN_PROGRESS))
					.setProgressMessageId(referenceMessageId);
			task = this.discordLoadBalancer.findRunningTask(condition).findFirst().orElse(null);
			if (task != null) {
				task.fail("[" + title + "] " + description);
				task.awake();
			}
		}
		if(null != task && task.getSubType() == 2){
			User user = userService.getById(task.getUserId());
			WxMpKefuMessage wxMpKefuMessage = WxMpKefuMessage.TEXT().toUser(user.getFromUserName()).content(description).build();
			wxMpService.getKefuService().sendKefuMessage(wxMpKefuMessage);
		}
		asyncService.updateMjTask(task);
	}


}
