package com.chat.java.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class SdCreateReq {

    @ApiModelProperty("正面提示词")
    @NotNull(message = "正面提示词不能为空")
    private String prompt;

    @ApiModelProperty("负面提示词")
    private String negativePrompt;

    @ApiModelProperty("步数")
    @NotNull(message = "步数不能为空")
    private Integer steps;

    @ApiModelProperty("宽512")
    private Integer width = 512;

    @ApiModelProperty("高默认512")
    private Integer height = 512;

    @ApiModelProperty("cfgScale默认7")
    private Integer cfgScale = 7;

    @ApiModelProperty("生成图片数量默认1")
    @Max(value = 4)
    private Integer batchSize = 1;

    @ApiModelProperty("seed")
    private Integer seed = -1;

    @ApiModelProperty("sampler_index")
    private String samplerIndex = "Euler a";

    @ApiModelProperty("eta")
    private String eta ;


}
