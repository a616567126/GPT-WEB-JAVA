package com.intelligent.bot.service.mj;


import com.intelligent.bot.model.res.mj.GetTaskRes;

import java.io.IOException;

public interface DiscordMessageService {

    GetTaskRes getMjMessages(Long id) throws IOException;
}
