package com.intelligent.bot.model.req.bing;

import lombok.Data;

@Data
public class BingChatReq {

    /**
     * 问题
     */
    private String prompt;


    /**
     * h3precise -- 准确模式 h3imaginative -- 创造模式 harmonyv3 -- 均衡模式
     */
    private String mode;

    /**
     * 消息id
     */
    private Long logId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 是否正确答案1正确 0 错误
     */
    private Integer isOk = 0;




}
