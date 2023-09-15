package com.intelligent.bot.model.bard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BardResponse {
    private final String conversationId;
    private final String responseId;
    private final String choiceId;
    private final Answer answer;
}
