package com.intelligent.bot.api.sse;

import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")

@Log4j2
public class SseController {

    /**
     * 用于创建连接
     */
    @GetMapping("/connect/{userId}")
    public SseEmitter connect(@PathVariable Long userId) {
        String redisToken  = RedisUtil.getCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + userId);
        if(StringUtils.isEmpty(redisToken)){
            return null;
        }
        return SseEmitterServer.connect(userId);
    }

    /**
     * 关闭连接
     */
    @GetMapping("/close/{userId}")
    public B<String> close(@PathVariable("userId") Long userId) {
        SseEmitterServer.removeUser(userId);
        return B.okBuild("连接关闭");
    }
}
