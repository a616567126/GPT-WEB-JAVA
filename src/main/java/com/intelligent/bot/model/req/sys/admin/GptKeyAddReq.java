package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;

@Data
public class GptKeyAddReq {


    /**
     * key
     */
    private String key;


    /**
     * 使用次数
     */
    private Integer useNumber;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0 启用 1禁用
     */
    private Integer state;
}
