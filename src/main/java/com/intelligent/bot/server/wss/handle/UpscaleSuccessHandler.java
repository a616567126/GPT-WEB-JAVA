package com.intelligent.bot.server.wss.handle;

import com.intelligent.bot.api.midjourney.support.TaskCondition;
import com.intelligent.bot.enums.mj.MessageType;
import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.model.mj.data.ContentParseData;
import com.intelligent.bot.utils.mj.ConvertUtils;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * upscale消息处理.
 * 完成(create): **cat** - Upscaled (Beta或Light) by <@1083152202048217169> (fast)
 * 完成(create): **cat** - Upscaled by <@1083152202048217169> (fast)
 * 完成(create): **cat** - Image #1 <@1012983546824114217>
 */
@Component
public class UpscaleSuccessHandler extends MessageHandler {
	private static final String CONTENT_REGEX_1 = "\\*\\*(.*?)\\*\\* - Upscaled \\(.*?\\) by <@\\d+> \\((.*?)\\)";
	private static final String CONTENT_REGEX_2 = "\\*\\*(.*?)\\*\\* - Upscaled by <@\\d+> \\((.*?)\\)";
	private static final String CONTENT_REGEX_3 = "\\*\\*(.*?)\\*\\* - Image #\\d <@\\d+>";

	@Override
	public void handle(MessageType messageType, DataObject message) {
		String content = getMessageContent(message);
		ContentParseData parseData = getParseData(content);
		if (MessageType.CREATE.equals(messageType) && parseData != null && hasImage(message)) {
			TaskCondition condition = new TaskCondition()
					.setActionSet(Arrays.asList(TaskAction.UPSCALE))
					.setFinalPromptEn(parseData.getPrompt());
			findAndFinishImageTask(condition, parseData.getPrompt(), message);
		}
	}

	private ContentParseData getParseData(String content) {
		ContentParseData parseData = ConvertUtils.parseContent(content, CONTENT_REGEX_1);
		if (parseData == null) {
			parseData = ConvertUtils.parseContent(content, CONTENT_REGEX_2);
		}
		if (parseData != null) {
			return parseData;
		}
		Matcher matcher = Pattern.compile(CONTENT_REGEX_3).matcher(content);
		if (!matcher.find()) {
			return null;
		}
		parseData = new ContentParseData();
		parseData.setPrompt(matcher.group(1));
		parseData.setStatus("done");
		return parseData;
	}

}
