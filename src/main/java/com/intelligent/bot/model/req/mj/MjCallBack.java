package com.intelligent.bot.model.req.mj;

import com.intelligent.bot.enums.mj.TaskStatus;
import lombok.Data;


@Data
public class MjCallBack {

    /**
     * 图片url
     */
    private String imageUrl;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * id
     */
    private Long id;

    /**
     * 任务状态
     */
    private TaskStatus status;
}
