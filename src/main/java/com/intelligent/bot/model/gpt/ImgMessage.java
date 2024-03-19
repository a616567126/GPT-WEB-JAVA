package com.intelligent.bot.model.gpt;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.intelligent.bot.utils.sys.DateUtil;
import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImgMessage {
    /**
     * 目前支持三种角色参考官网，进行情景输入：https://platform.openai.com/docs/guides/chat/introduction
     */
    private String role;
    private List<JSONObject> content;
    private String name;
    private String time;

    public ImgMessage(String role, List<JSONObject> content) {
        this.role = role;
        this.content = content;
        this.time = DateUtil.getLocalDateTimeNow();
    }

    public static ImgMessage of(List<JSONObject> content) {
        return new ImgMessage(Role.USER.getValue(), content);
    }


    public static ImgMessage ofSystem(List<JSONObject> content) {
        return new ImgMessage(Role.SYSTEM.getValue(),content);
    }

    public static ImgMessage ofAssistant(List<JSONObject> content) {
        return new ImgMessage(Role.ASSISTANT.getValue(), content);
    }
    
    @Getter
    @AllArgsConstructor
    public enum Role {

        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        ;
        private final String value;
    }

}
