package com.intelligent.bot.model.res.stablestudio;

import lombok.Data;

@Data
public class TextToImageRes {

    /**
     * 图片base64编码
     */
    private String base64;

    /**
     * 完成原因
     * SUCCESS 表示成功
     * ERROR 指示错误
     * CONTENT_FILTERED 表示受内容过滤器影响的结果，可能会模糊。
     */
    private String finishReason;

    /**
     * 种子数
     */
    private Long seed;

    /**
     * 图片地址
     */
    private String imgUrl;
}
