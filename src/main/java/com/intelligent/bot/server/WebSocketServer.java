package com.intelligent.bot.server;

import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint("/webSocket/{userId}")
@Log4j2
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<Long, WebSocketServer> chatWebSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private Long userId = 0L;




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
    public void onMessage(String message, Session session){

    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(Object message) throws IOException {
        this.session.getBasicRemote().sendText(JSONObject.toJSONString(message));
    }

    public static void sendInfo(Long toUserId,Object message) throws IOException {
        chatWebSocketMap.get(toUserId).sendMessage(message);
    }
}
