package com.intelligent.bot.service.mj;


import com.intelligent.bot.base.result.B;
import com.intelligent.bot.enums.mj.BlendDimensions;
import eu.maxschuster.dataurl.DataUrl;

import java.util.List;

public interface DiscordService {

	B<Void> imagine(String prompt, String nonce);

	B<Void> upscale(String messageId, int index, String messageHash, int messageFlags, String nonce);

	B<Void> variation(String messageId, int index, String messageHash, int messageFlags, String nonce);

	B<Void> reroll(String messageId, String messageHash, int messageFlags, String nonce);

	B<Void> describe(String finalFileName, String nonce);

	B<Void> blend(List<String> finalFileNames, BlendDimensions dimensions, String nonce);

	B<String> upload(String fileName, DataUrl dataUrl);

	B<String> sendImageMessage(String content, String finalFileName);

}
