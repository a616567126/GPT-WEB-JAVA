package com.intelligent.bot.service.mj;


import com.intelligent.bot.base.result.B;
import eu.maxschuster.dataurl.DataUrl;

public interface DiscordService {

	B<Void> imagine(String prompt);

	B<Void> upscale(String messageId, int index, String messageHash);

	B<Void> variation(String messageId, int index, String messageHash);

	B<Void> reset(String messageId, String messageHash);

	B<String> upload(String fileName, DataUrl dataUrl);

	B<Void> describe(String finalFileName);

}
