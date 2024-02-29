package com.intelligent.bot.service.gpt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.gpt.Message;
import com.intelligent.bot.utils.gpt.TokensUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletion implements Serializable {

    @NonNull
    @Builder.Default
    private String model = Model.GPT_3_5_TURBO_16K.getName();

    @NonNull
    private List<Message> messages;
    /**
     * 使用什么取样温度，0到2之间。越高越奔放。越低越保守。
     * <p>
     * 不要同时改这个和topP
     */
    @Builder.Default
    private double temperature = 0.9;

    /**
     * 0-1
     * 建议0.9
     * 不要同时改这个和temperature
     */
    @JsonProperty("top_p")
    @Builder.Default
    private double topP = 0.9;


    /**
     * 结果数。
     */
    @Builder.Default
    private Integer n = 1;


    /**
     * 是否流式输出.
     * default:false
     */
    @Builder.Default
    private boolean stream = false;
    /**
     * 停用词
     */
    private List<String> stop;
    /**
     * 3.5 最大支持4096
     * 4.0 最大32k
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;


    @JsonProperty("presence_penalty")
    private double presencePenalty;

    /**
     * -2.0 ~~ 2.0
     */
    @JsonProperty("frequency_penalty")
    private double frequencyPenalty;

    @JsonProperty("logit_bias")
    private Map logitBias;
    /**
     * 用户唯一值，确保接口不被重复调用
     */
    private String user;


    @Getter
    @AllArgsConstructor
    public enum Model {
        /**
         * gpt-3.5-turbo
         */
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        /**
         * 临时模型，不建议使用，2023年9 月 13 日将被弃用
         */
        @Deprecated
        GPT_3_5_TURBO_0301("gpt-3.5-turbo-0301"),
        /**
         * gpt-3.5-turbo-0613 支持函数
         */
        GPT_3_5_TURBO_0613("gpt-3.5-turbo-0613"),
        /**
         * gpt-3.5-turbo-16k 超长上下文
         */
        GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"),
        /**
         * gpt-3.5-turbo-16k-0613 超长上下文 支持函数
         */
        GPT_3_5_TURBO_16K_0613("gpt-3.5-turbo-16k-0613"),
        /**
         * gpt-3.5-turbo-1106 最新的 GPT-3.5 Turbo 模型具有改进的指令跟踪、JSON 模式、可重现的输出、并行函数调用等。
         */
        GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"),
        /**
         * GPT4.0
         */
        GPT_4("gpt-4"),
        /**
         * 临时模型，不建议使用，2023年9 月 13 日将被弃用
         */
        @Deprecated
        GPT_4_0314("gpt-4-0314"),
        /**
         * GPT4.0 超长上下文
         */
        GPT_4_32K("gpt-4-32k"),
        /**
         * 临时模型，不建议使用，2023年9 月 13 日将被弃用
         */
        @Deprecated
        GPT_4_32K_0314("gpt-4-32k-0314"),

        /**
         * gpt-4-0613，支持函数
         */
        GPT_4_0613("gpt-4-0613"),
        /**
         * gpt-4-0613，支持函数
         */
        GPT_4_32K_0613("gpt-4-32k-0613"),
        /**
         * 支持数组模式，支持function call，支持可重复输出
         */
        GPT_4_1106_PREVIEW("gpt-4-1106-preview"),
        /**
         * 支持图片
         */
        GPT_4_VISION_PREVIEW("gpt-4-vision-preview"),
        ;
        private final String name;
    }

    public int countTokens() {
        return TokensUtil.tokens(this.model, this.messages);
    }
    public boolean checkTokens(){
        int tokens = this.countTokens();
        return this.model.startsWith("gpt-3.5-turbo") && tokens > CommonConst.GPT_3_5_TURBO_0301_TOKENS;
    }
}


