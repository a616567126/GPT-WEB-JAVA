package com.intelligent.bot.listener;

import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.GptKey;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.IGptKeyService;
import com.intelligent.bot.service.sys.IUserService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Builder
public class ConsoleStreamListener extends AbstractStreamListener {

    final Long userId;

    final AsyncService asyncService;

    final IUserService userService;

    final IGptKeyService gptKeyService;

    @Override
    public void onMsg(Object message) {
        SseEmitterServer.sendMessage(userId, message);
    }

    @Override
    public void onError(Throwable throwable, String response) {
        //修改key状态
        asyncService.updateKeyState(response);
        //将用户使用次数返回
        GptKey gptKey = gptKeyService.lambdaQuery().eq(GptKey::getKey, response).one();
        asyncService.updateRemainingTimes(userId,  gptKey.getType() == 3 ? CommonConst.GPT_NUMBER : CommonConst.GPT_4_NUMBER);
        log.error("gpt对话异常，异常key：{}",response);
        JSONObject errorMessage = new JSONObject();
        errorMessage.put("content","AI对话服务异常请稍后再试");
        SseEmitterServer.sendMessage(userId, errorMessage);
    }

}
