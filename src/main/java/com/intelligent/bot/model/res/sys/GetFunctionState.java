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
}
