package com.intelligent.bot.model.res.sys;

import com.intelligent.bot.enums.mj.TaskAction;
import com.intelligent.bot.enums.mj.TaskStatus;
import lombok.Data;

import java.util.List;

@Data
public class MjTaskRes  {
    /**
     * 任务类型
     */
    private TaskAction taskAction;


    /**
     * 关联任务id
     */
    private String relatedTaskId;

    /**
     * 关键字
     */
    private String prompt;

    /**
     * 译文
     */
    private String promptEn;

    /**
     * 图片位置
     */
    private Integer index;

    /**
     * 任务状态
     */
    private TaskStatus status;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 发起时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long finishTime;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * mj 任务信息
     */
    private String finalPrompt;

    /**
     * 变换位置信息
     */
    List<MjTaskTransformRes> taskTransformList;

    private Long id;
}
