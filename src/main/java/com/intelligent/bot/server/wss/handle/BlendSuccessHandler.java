package com.intelligent.bot.server.wss.handle;


import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.mj.data.ContentParseData;
import com.intelligent.bot.utils.mj.ConvertUtils;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * blend消息处理.
 * 完成(create): **<https://s.mj.run/JWu6jaL1D-8> <https://s.mj.run/QhfnQY-l68o> --v 5.1** - <@1012983546824114217> (relaxed)
 */
@Component
public class BlendSuccessHandler extends MessageHandler {

	@Override
	public void handle(MessageType messageType, DataObject message) {
		String content = getMessageContent(message);
		ContentParseData parseData = ConvertUtils.parseContent(content);
		if (parseData == null || !MessageType.CREATE.equals(messageType)) {
			return;
		}
		Optional<DataObject> interaction = message.optObject("interaction");
		if (interaction.isPresent() && "blend".equals(interaction.get().getString("name"))) {
			// blend任务开始时，设置prompt
			Task task = this.discordLoadBalancer.getRunningTaskByNonce(getMessageNonce(message));
			if (task != null) {
				task.setPromptEn(parseData.getPrompt());
				task.setPrompt(parseData.getPrompt());
			}
		}
		if (hasImage(message)) {
			TaskCondition condition = new TaskCondition()
					.setActionSet(Arrays.asList(TaskAction.BLEND))
					.setFinalPromptEn(parseData.getPrompt());
			findAndFinishImageTask(condition, parseData.getPrompt(), message);
		}
	}

}
