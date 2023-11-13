package com.intelligent.bot.model.req.gpt;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GptAlphaReq {
    @NotBlank(message = "消息数据不能为空")
    /**
     * 提示词：dall-e-2支持1000字符、dall-e-3支持4000字符
     */
    @NotBlank(message = "提示词不能为空")
    private String prompt;

    /**
     * 模型
     * 支持dall-e-2、dall-e-3
     */
    private String model = "dall-e-3";

    /**
     * 图片尺寸
     * dall-e-2支持：256x256, 512x512, or 1024x1024
     * dall-e-3支持：1024x1024, 1792x1024, or 1024x1792
     */
    private String size = "1024x1024";

    /**
     * 为每个提示生成的个数，dall-e-3只能为1。
     */
    private Integer n = 1;

    /**式
     * 生成图片格式：url、b64_json
     */
    private String response_format = "url";

    /**
     * 图片生成质量 standard ，hd
     *
     */
    private String quality = "hd";

    /**
     * 此参数仅仅dall-e-3,取值范围：vivid、natural
     * 默认值：vivid
     */
    private String style = "vivid";
}
