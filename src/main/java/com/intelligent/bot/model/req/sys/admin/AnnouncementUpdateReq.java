package com.intelligent.bot.model.req.sys.admin;

import lombok.Data;


@Data
public class AnnouncementUpdateReq {


    /**
     * key
     */
    private String title;
    /**
     * 内容
     */

    private String content;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * id
     */
    private Long id;
}
