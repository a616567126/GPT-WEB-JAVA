package com.intelligent.bot.model.res.sys.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardPinQueryRes {

    /**
     * 卡密
     */
    private String cardPin;

    /**
     * 次数
     */
    private Integer number;

    /**
     * 使用用户
     */
    private String userName;

    /**
     * 状态 0 未使用 1使用
     */
    private Integer state;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 卡密id
     */
    private Long id;
}
