package com.intelligent.bot.utils.sys;

import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.server.WebSocketServer;

import java.io.IOException;

public class SendMessageUtil {

    public static void sendMessage(Long userId, Object message) {
        Integer isMobile  = RedisUtil.getCacheObject(CommonConst.USER_CLIENT + userId);
        if(null == isMobile){
            isMobile = 0;
        }
        if (isMobile == 1) {
            try {
                WebSocketServer.sendInfo(userId, message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            SseEmitterServer.sendMessage(userId, message);
        }
    }
}
