package com.intelligent.bot.listener;

import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.IUserService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Builder
public class ConsoleStreamListener extends AbstractStreamListener {

    final Long userId;

    final AsyncService asyncService;

    final IUserService userService;

    @Override
    public void onMsg(Object message) {
        SseEmitterServer.sendMessage(userId, message);
    }

    @Override
    public void onError(Throwable throwable, String response) {
        //修改key状态
        asyncService.updateKeyState(response);
        //将用户使用次数返回
        asyncService.updateRemainingTimes(userId, CommonConst.GPT_NUMBER);
        log.error("gpt对话异常，异常key：{}",response);
        SseEmitterServer.sendMessage(userId, "AI对话服务异常请稍后再试");
    }

}
