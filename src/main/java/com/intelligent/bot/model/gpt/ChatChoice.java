package com.intelligent.bot.model.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class ChatChoice {
    private long index;
    /**
     * 请求参数stream为true返回是delta
     */
    @JsonProperty("delta")
    private Message delta;
    /**
     * 请求参数stream为false返回是message
     */
    @JsonProperty("message")
    private Message message;

    @JsonProperty("finish_reason")
    private String finishReason;
}
