package com.intelligent.bot.model.req.gpt;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GptAlphaReq {
    @NotBlank(message = "消息数据不能为空")
    private String prompt;

    /**
     * model
     */
    private String model = "image-alpha-001";

    /**
     * imageSize
     */
    private String size = "512x512";

    /**
     * numberOfImages
     */
    private Integer num_images = 1;

    /**
     * imageType
     */
    private String response_format = "url";

    private Integer type = 3;
}
