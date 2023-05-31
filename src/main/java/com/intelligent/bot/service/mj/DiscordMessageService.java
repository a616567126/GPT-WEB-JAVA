package com.intelligent.bot.service.mj;


import com.intelligent.bot.model.res.mj.GetTaskRes;

public interface DiscordMessageService {

    GetTaskRes getMjMessages(String taskId);
}
