package com.intelligent.bot.service.gpt;

import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.model.gpt.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.net.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;




@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTStream {

    private String apiKey;

    private OkHttpClient okHttpClient;
    /**
     * 连接超时
     */
    @Builder.Default
    private long timeout = 90;

    /**
     * 网络代理
     */
    @Builder.Default
    private Proxy proxy = Proxy.NO_PROXY;
    /**
     * 反向代理
     */
    @Builder.Default
    private String apiHost ="https://api.openai.com/";

    /**
     * 初始化
     */
    public ChatGPTStream init() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(timeout, TimeUnit.SECONDS);
        client.writeTimeout(timeout, TimeUnit.SECONDS);
        client.readTimeout(timeout, TimeUnit.SECONDS);
        if (Objects.nonNull(proxy)) {
            client.proxy(proxy);
        }
        okHttpClient = client.build();
        return this;
    }


    /**
     * 流式输出
     */
    public void streamChatCompletion(ChatCompletion chatCompletion,
                                     EventSourceListener eventSourceListener) {

        chatCompletion.setStream(true);

        try {
            EventSource.Factory factory = EventSources.createFactory(okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(chatCompletion);
            String key = apiKey;
            Request request = new Request.Builder()
                    .url(apiHost + "v1/chat/completions")
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()),
                            requestBody))
                    .header("Authorization", "Bearer " + key)
                    .build();
            factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求出错：{}", e);
        }
    }

    /**
     * 流式输出
     */
    public void streamChatCompletion(List<Message> messages,
                                     EventSourceListener eventSourceListener) {
        messages.forEach(m ->{
            m.setTime(null);
        });
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .messages(messages)
                .stream(true)
                .build();
        if(chatCompletion.checkTokens()){
            throw new E("本次会话长度达到限制，请创建新的会话");
        }
        streamChatCompletion(chatCompletion, eventSourceListener);
    }

}
