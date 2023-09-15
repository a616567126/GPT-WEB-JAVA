package com.intelligent.bot.model.bard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import static com.intelligent.bot.constant.CommonConst.EMPTY_STRING;


@AllArgsConstructor
@Getter
@Setter
public class BardRequest {
    private String strSNlM0e;
    private String question;
    private String conversationId;
    private String responseId;
    private String choiceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BardRequest that = (BardRequest) o;
        return strSNlM0e.equals(that.strSNlM0e) &&
                question.equals(that.question) &&
                conversationId.equals(that.conversationId) &&
                responseId.equals(that.responseId) &&
                choiceId.equals(that.choiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strSNlM0e, question, conversationId, responseId, choiceId);
    }

    @Override
    public String toString() {
        return "BardRequest{" +
                "strSNlM0e=" + strSNlM0e +
                ", question=" + question +
                ", conversationId=" + conversationId +
                ", responseId=" + responseId +
                ", choiceId=" + choiceId +
                '}';
    }

    public static BardRequest newEmptyBardRequest() {
        return new BardRequest(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);
    }
}
