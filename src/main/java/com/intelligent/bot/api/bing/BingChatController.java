package com.intelligent.bot.api.bing;


import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.enums.sys.SendType;
import com.intelligent.bot.model.MessageLog;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.model.req.bing.BingChatReq;
import com.intelligent.bot.server.SseEmitterServer;
import com.intelligent.bot.service.bing.BingChatService;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.CheckService;
import com.intelligent.bot.service.sys.IMessageLogService;
import com.intelligent.bot.utils.sys.JwtUtil;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/bing")
@Log4j2
@Transactional(rollbackFor = E.class)
public class BingChatController {

    @Resource
    BingChatService bingChatService;
    @Resource
    CheckService checkService;
    @Resource
    AsyncService asyncService;
    @Resource
    IMessageLogService messageLogService;

    @RequestMapping(value = "/chat", name = "bing对话")
    public B<String> bingChat(@Validated @RequestBody BingChatReq req) throws ExecutionException, InterruptedException {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        if(sysConfig.getIsOpenBing() == 0){
            SseEmitterServer.sendMessage(JwtUtil.getUserId(),"暂未开启newBing");
        }
        List<Message> messages = messageLogService.createMessageLogList(req.getLogId(), req.getPrompt());
        Long logId = checkService.checkUser(MessageLog.builder()
                .useNumber(CommonConst.BING_NUMBER)
                .sendType(SendType.BING.getType())
                .useValue(JSONObject.toJSONString(messages))
                .userId(JwtUtil.getUserId()).build());
        CompletableFuture<String> completableFuture = bingChatService.ask(req);
        String bingMessage = "异常对话，请稍后重试";
        if(!StringUtils.isEmpty(completableFuture.get())){
            bingMessage = completableFuture.get();
        }else {
            asyncService.updateRemainingTimes(JwtUtil.getUserId(), CommonConst.BING_NUMBER);
        }
        asyncService.endOfAnswer(logId,bingMessage);
        return B.okBuild("请求成功，请稍后");
    }
}
