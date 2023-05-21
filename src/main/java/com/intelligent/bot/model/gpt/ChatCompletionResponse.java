package com.intelligent.bot.model.gpt;

import com.intelligent.bot.model.gpt.billing.Usage;
import lombok.Data;

import java.util.List;


@Data
public class ChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChatChoice> choices;
    private Usage usage;
}
