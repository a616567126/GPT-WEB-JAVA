package com.intelligent.bot.listener.spark;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.bot.enums.spark.Status;
import com.intelligent.bot.model.spark.AIChatRequest;
import com.intelligent.bot.model.spark.AIChatResponse;
import com.intelligent.bot.model.spark.OutHeader;
import com.intelligent.bot.model.spark.Usage;
import io.reactivex.rxjava3.annotations.Nullable;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 默认的web socket监听
 * 控制台打印输出信息
 *
 * @author https://www.unfbx.com/
 */
@Getter
@Slf4j
public abstract class ChatListener extends WebSocketListener {

    /**
     * 请求大模型的参数
     */
    private AIChatRequest aiChatRequest;

    /**
     * 构造方法，传入大模型参数
     *
     * @param aiChatRequest 大模型参数
     */
    public ChatListener(AIChatRequest aiChatRequest) {
        this.aiChatRequest = aiChatRequest;
    }

    /**
     * WebSocket服务发生异常的回调，可以覆盖重写。
     * 默认抛出异常
     *
     * @param t        异常
     * @param response 返回值
     */
    public void onWebSocketError(Throwable t, Response response) {
        log.error("调用星火模型时，WebSocket发生异常:{}", response);
        t.printStackTrace();
    }

    /**
     * 星火大模型发生异常
     *
     * @param aiChatResponse 大模型返回值
     */
    public abstract void onChatError(AIChatResponse aiChatResponse);

    /**
     * 星火大模型正常返回信息
     *
     * @param aiChatResponse 大模型返回值
     */
    public abstract void onChatOutput(AIChatResponse aiChatResponse);

    /**
     * 星火大模型返回信息结束回调
     */
    public abstract void onChatEnd();

    /**
     * 星火大模型本次请求消耗的Token信息
     *
     * @param usage 大模型返回token信息
     */
    public abstract void onChatToken(Usage usage);

    /**
     * 构造星火大模型请求参数，默认使用构造方法传入的信息
     * 可以覆盖重写
     *
     * @return 大模型请求参数
     */
    public AIChatRequest onChatSend() {
        return this.aiChatRequest;
    }

    @Override
    public final void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public final void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public final void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        webSocket.close(1000, "");
        this.onWebSocketError(t, response);
    }

    @Override
    public final void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        AIChatResponse aiChatResponse = JSONUtil.toBean(text, AIChatResponse.class);

        if (OutHeader.Code.SUCCESS.getValue() != aiChatResponse.getHeader().getCode()) {
            log.warn("调用星火模型发生错误，错误码为：{}，请求的sid为：{}", aiChatResponse.getHeader().getCode(), aiChatResponse.getHeader().getSid());
            webSocket.close(1000, "星火模型调用异常");
            this.onChatError(aiChatResponse);
            return;
        }

        this.onChatOutput(aiChatResponse);

        if (Status.END.getValue() == aiChatResponse.getHeader().getStatus()) {
            // 可以关闭连接，释放资源
            webSocket.close(1000, "星火模型返回结束");
            Usage usage = aiChatResponse.getPayload().getUsage();
            this.onChatEnd();
            this.onChatToken(usage);
        }
    }

    @Override
    public final void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    @SneakyThrows
    @Override
    public final void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        AIChatRequest aiChatRequest = this.onChatSend();
        ObjectMapper mapper = new ObjectMapper();
        webSocket.send(mapper.writeValueAsString(Objects.isNull(aiChatRequest) ? this.getAiChatRequest() : aiChatRequest));
    }
}
