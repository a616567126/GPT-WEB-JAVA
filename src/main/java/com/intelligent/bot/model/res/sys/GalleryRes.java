package com.intelligent.bot.model.res.sys;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GalleryRes {

    /**
     * 咒语翻译
     */
    private String promptEn;

    /**
     * 咒语
     */
    private String prompt;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 生成时间
     */
    private LocalDateTime createTime;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 昵称
     */
    private String name;
}
