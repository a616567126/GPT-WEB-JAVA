package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;


@Data
public class SdLoraUpdateReq {


    /**
     * lora名
     */
    private String loraName;

    /**
     * lora图片地址
     */
    private String imgUrl;

    /**
     * id
     */
    private Long id;


}


