package com.intelligent.bot.service.mj;


import com.intelligent.bot.base.result.B;
import com.intelligent.bot.enums.mj.BlendDimensions;
import eu.maxschuster.dataurl.DataUrl;

import java.util.List;

public interface DiscordService {

	B<Void> imagine(String prompt);

	B<Void> upscale(String messageId, int index, String messageHash, int messageFlags);

	B<Void> variation(String messageId, int index, String messageHash, int messageFlags);

	B<Void> reroll(String messageId, String messageHash, int messageFlags);

	B<Void> describe(String finalFileName);

	B<Void> blend(List<String> finalFileNames, BlendDimensions dimensions);

	B<String> upload(String fileName, DataUrl dataUrl);

	B<String> sendImageMessage(String content, String finalFileName);

}
