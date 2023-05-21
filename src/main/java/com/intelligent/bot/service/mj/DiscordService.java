package com.intelligent.bot.service.mj;



import com.intelligent.bot.base.result.B;

public interface DiscordService {

	B<Void> imagine(String prompt);

	B<Void> upscale(String messageId, int index, String messageHash);

	B<Void> variation(String messageId, int index, String messageHash);

	B<Void> reset(String messageId, String messageHash);

}