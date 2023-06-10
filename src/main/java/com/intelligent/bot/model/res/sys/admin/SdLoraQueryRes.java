package com.intelligent.bot.model.res.sys.admin;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class SdLoraQueryRes {



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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}


