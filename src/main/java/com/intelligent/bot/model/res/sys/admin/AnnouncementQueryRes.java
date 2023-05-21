package com.intelligent.bot.model.res.sys.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementQueryRes {

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
     * 创建时间
     */
    private LocalDateTime createTime;
}
