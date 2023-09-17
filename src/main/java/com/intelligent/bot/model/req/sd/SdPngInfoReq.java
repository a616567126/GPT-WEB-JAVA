package com.intelligent.bot.model.req.sd;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SdPngInfoReq {

    /**
     * 图片base64地址
     */
    @NotNull
    private String image;
}
