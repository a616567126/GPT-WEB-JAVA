package com.intelligent.bot.model.gpt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.intelligent.bot.utils.sys.DateUtil;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    /**
     * 目前支持三种角色参考官网，进行情景输入：https://platform.openai.com/docs/guides/chat/introduction
     */
    private String role;
    private String content;
    private String name;
    private String time;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
        this.time = DateUtil.getLocalDateTimeNow();
    }

    public static Message of(String content) {
        return new Message(Role.USER.getValue(), content);
    }

    public static Message ofSystem(String content) {
        return new Message(Role.SYSTEM.getValue(),content);
    }

    public static Message ofAssistant(String content) {
        return new Message(Role.ASSISTANT.getValue(), content);
    }
    
    @Getter
    @AllArgsConstructor
    public enum Role {

        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        ;
        private String value;
    }

}
