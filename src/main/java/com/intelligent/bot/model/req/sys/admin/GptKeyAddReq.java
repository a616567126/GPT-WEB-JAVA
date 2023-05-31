package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;

@Data
public class GptKeyAddReq {


    /**
     * key
     */
    private String key;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0 启用 1禁用
     */
    private Integer state;

    /**
     * key类型 3-gpt3.5 4-gpt4
     */
    private Integer type;
}
