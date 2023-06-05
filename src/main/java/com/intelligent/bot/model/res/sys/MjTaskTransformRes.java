package com.intelligent.bot.model.res.sys;

import com.intelligent.bot.enums.mj.TaskAction;
import lombok.Data;

@Data
public class MjTaskTransformRes {

    /**
     * 任务类型
     */
    private TaskAction action;


    /**
     * 图片位置
     */
    private Integer index;

    /**
     * 关联任务id
     */
    private Long relatedTaskId;

}
