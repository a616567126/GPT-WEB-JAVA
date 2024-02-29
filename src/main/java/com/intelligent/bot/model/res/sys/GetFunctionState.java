package com.intelligent.bot.model.res.sys;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetFunctionState {

    /**
     * 是否开启sd 0未开启 1开启
     */
    private Integer isOpenSd;


    /**
     * 是否开启FlagStudio 0-未开启 1开启
     */
    private Integer isOpenFlagStudio;

    /**
     * 是否开启Mj 0-未开启 1开启
     */
    private Integer isOpenMj;

    /**
     * 是否开启bing 0未开启 1开启
     */
    private Integer isOpenBing;

    /**
     * gpt开关 0-未开启、1-开启gpt3.5、2-开启gpt4.0、3-全部
     */
    private Integer isOpenGpt;


    /**
     * gpt画图开关 0-未开启、1-开启
     */
    private Integer isOpenGptOfficial;

    /**
     * 星火模型开关 0 -未开启 、1-开启
     */
    private Integer isOpenSpark;


    /**
     * gpt4内容识别请求地址
     */
    private Integer isOpenGptVision;
}
