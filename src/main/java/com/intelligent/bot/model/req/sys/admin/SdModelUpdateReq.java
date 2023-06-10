package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;


@Data
public class SdModelUpdateReq {


    /**
     * 模型名
     */
    private String modelName;

    /**
     * 模型图片地址
     */
    private String imgUrl;

    /**
     * id
     */
    private Long id;


}


