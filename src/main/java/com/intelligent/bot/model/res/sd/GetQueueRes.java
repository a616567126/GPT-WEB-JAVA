package com.intelligent.bot.model.res.sd;

import lombok.Data;

@Data
public class GetQueueRes {

    /**
     * 当前用户队列状态 1排队中 2画图中
     */
    private Integer state;

    /**
     * 消息
     */
    private String message;

    /**
     * 画图时base64文件内容
     */
    private String img;

    /**
     * 画图时百分比
     */
    private Double progress;

    /**
     * 排队人数
     */
    private Long queueSize;

    /**
     * 当前位置
     */
    private Integer position;

}
