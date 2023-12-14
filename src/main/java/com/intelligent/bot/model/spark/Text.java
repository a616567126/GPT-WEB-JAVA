package com.intelligent.bot.model.spark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Text {
    /**
     * 取值为[user,assistant]
     */
    private String role;
    /**
     * 所有content的累计tokens需控制8192以内
     */
    private String content;
    /**
     * 结果序号，取值为[0,10]; 当前为保留字段，开发者可忽略
     */
    private Integer index;

    private LocalDateTime time = LocalDateTime.now();

    @Getter
    public enum Role {

        USER("user"),
        ASSISTANT("assistant"),
        ;

        Role(String name) {
            this.name = name;
        }

        private final String name;
    }

}
