package com.cn.app.chatgptbot.flow.server;

import cn.hutool.core.bean.BeanUtil;
import com.cn.app.chatgptbot.base.Result;
import com.cn.app.chatgptbot.constant.CommonConst;
import com.cn.app.chatgptbot.flow.chat.ChatRequestParameter;
import com.cn.app.chatgptbot.flow.model.ChatModel;
import com.cn.app.chatgptbot.flow.service.CheckService;
import com.cn.app.chatgptbot.model.UseLog;
import com.cn.app.chatgptbot.service.AsyncLogService;
import com.cn.app.chatgptbot.service.IUseLogService;
import com.cn.app.chatgptbot.utils.GptUtil;
import com.cn.app.chatgptbot.utils.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/chatWebSocket/{userId}")
@Log4j2
public class ChatWebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<Long, ChatWebSocketServer> chatWebSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private Long userId = 0L;

    private UseLog useLog;

    private static AsyncLogService asyncLogService;


    private static CheckService checkService;


    @Resource
    public void setAsyncLogService(AsyncLogService asyncLogService) {
        ChatWebSocketServer.asyncLogService = asyncLogService;
    }

    private ObjectMapper objectMapper = new ObjectMapper();
    private static ChatModel chatModel;

    @Resource
    public void setChatModel(ChatModel chatModel) {
        ChatWebSocketServer.chatModel = chatModel;
    }

    @Resource
    public void setCheckService(CheckService checkService) {
        ChatWebSocketServer.checkService = checkService;
    }

    ChatRequestParameter chatRequestParameter = new ChatRequestParameter();

    /**
     * 建立连接
     * @param session 会话
     * @param userId 连接用户id
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) throws IOException {
        String redisToken  = RedisUtil.getCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + userId);
        if(StringUtils.isEmpty(redisToken)){
            session.getBasicRemote().sendText("请先登录");
            return;
        }
        this.session = session;
        this.userId = userId;
        this.useLog = new UseLog();
        // 这里的用户id不可能为null，出现null，那么就是非法请求
        try {
            this.useLog.setUserId(Long.valueOf(userId));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        chatWebSocketMap.put(userId, this);
        onlineCount++;
        log.info(userId + "--open");
    }

    @OnClose
    public void onClose() {
        chatWebSocketMap.remove(userId);
        log.info(userId + "--close");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        final String mainKey = GptUtil.getMainKey();
        Result result = checkService.checkUser(mainKey, this.userId,session);
        if(result.getCode() != 20000){
            session.getBasicRemote().sendText(result.getMsg());
            return;
        }
        UseLog data = (UseLog) result.getData();
        BeanUtil.copyProperties(data,this.useLog);
        log.info(userId + "--" + message);
        // 记录日志
        this.useLog.setCreateTime(LocalDateTime.now());
        this.useLog.setQuestion(message);
        this.useLog.setSendType(1);
        // 这里就会返回结果
        String answer = chatModel.getAnswer(session, chatRequestParameter, message,mainKey);
        if(StringUtils.isEmpty(answer)){
            //将key删除
            GptUtil.removeKey(Collections.singletonList(mainKey));
            Integer resulCode = GptUtil.getRandomKey(mainKey);
            if(resulCode == -1){
                session.getBasicRemote().sendText("暂无可使用的key，请联系管理员");
            }else {
                asyncLogService.updateKeyNumber(mainKey,1);
            }
        }else {
            this.useLog.setAnswer(answer);
            asyncLogService.saveUseLog(useLog);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendInfo(String message, String toUserId) throws IOException {
        chatWebSocketMap.get(toUserId).sendMessage(message);
    }
}
