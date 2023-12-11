package com.intelligent.bot.service.spark;

import com.intelligent.bot.model.req.spark.ChatReq;

public interface ISparkService {

    public Long chat(ChatReq req);
}
