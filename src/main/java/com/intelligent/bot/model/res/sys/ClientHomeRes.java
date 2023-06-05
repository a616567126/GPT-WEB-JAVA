package com.intelligent.bot.model.res.sys;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ClientHomeRes {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 剩余次数
     */
    private Integer remainingTimes;


    /**
     * 对话记录
     */
    List<ClientHomeLogRes> logList;

    /**
     * 置顶公告
     */
    private String announcement;

    /**
     * mj任务列表
     */
    List<MjTaskRes> mjTaskList;

}
