package com.intelligent.bot.listener.gpt;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.model.gpt.ChatChoice;
import com.intelligent.bot.model.gpt.ChatCompletionResponse;
import com.intelligent.bot.model.gpt.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


@Slf4j
public abstract class AbstractStreamListener extends EventSourceListener {


    protected StringBuffer lastMessage = new StringBuffer();


    /**
     * Called when all new message are received.
     *
     * @param message the new message
     */
    @Setter
    @Getter
    protected Consumer<StringBuffer> onComplate = s -> {

    };

    /**
     * Called when a new message is received.
     * 收到消息 单个字
     *
     * @param message the new message
     */
    public abstract void onMsg(Object message) throws IOException;

    /**
     * Called when an error occurs.
     * 出错时调用
     *
     * @param throwable the throwable that caused the error
     * @param response  the response associated with the error, if any
     */
//    public abstract void onError(Throwable throwable, String response);
    public abstract void onError(Throwable throwable);

    @Override
    public void onOpen(EventSource eventSource, Response response) {
        // do nothing
    }

    @Override
    public void onClosed(EventSource eventSource) {
        // do nothing
    }

    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("回答中：{}", data);
        if (data.equals("[DONE]")) {
            onComplate.accept(lastMessage);
            onMsg(Message.ofAssistant("[DONE]"));
            return;
        }

        ChatCompletionResponse response = JSONObject.parseObject(data, ChatCompletionResponse.class);
        // 读取Json
        List<ChatChoice> choices = response.getChoices();
        if (choices == null || choices.isEmpty()) {
            return;
        }
        Message delta = choices.get(0).getDelta();
        String text = delta.getContent();
        if (!StringUtils.isEmpty(text)) {
            if(delta.getContent().equals("<!")){
                delta.setContent("```\\n");
                text = delta.getContent();
                lastMessage.append(text);
                onMsg(delta);
                delta.setContent("<!");
                text = delta.getContent();
                lastMessage.append(text);
                onMsg(delta);
            }else {
                lastMessage.append(text);
                onMsg(delta);
            }
        }
    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable throwable, Response response) {

        try {
            log.error("Stream connection error: {}", throwable);

            String responseText = "";

            if (Objects.nonNull(response)) {
                responseText = response.body().string();
            }
            log.error("GPT异常log:{}",responseText);
//            String[] auth = eventSource.request().headers().toString().split(" ");
//            String key = auth[2];
            String forbiddenText = "Your access was terminated due to violation of our policies";
            if (StrUtil.contains(responseText, forbiddenText)) {
                log.error("Chat session has been terminated due to policy violation");
                log.error("检测到号被封了");
            }
            String overloadedText = "That model is currently overloaded with other requests.";
            if (StrUtil.contains(responseText, overloadedText)) {
                log.error("检测到官方超载了，赶紧优化你的代码，做重试吧");
            }
//            this.onError(throwable, key.replaceAll("\\s*|\r|\n|\t",""));
            this.onError(throwable);
        } catch (Exception e) {
            log.warn("onFailure error:{}", e);
            // do nothing

        } finally {
            eventSource.cancel();
        }
    }
}
