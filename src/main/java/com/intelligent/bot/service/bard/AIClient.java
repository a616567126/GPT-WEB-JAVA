package com.intelligent.bot.service.bard;


import com.intelligent.bot.model.bard.Answer;

public interface AIClient {
    Answer ask(String question);
    void reset();
}
