package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.model.Task;


public interface IMjTaskService extends IService<Task> {


    /**
     * 清空 mj 任务
     * @param userId
     * @return
     */
    int emptyMjTask(Long userId);

    /**
     * 删除 mj 任务
     * @param id
     * @return
     */
    int deleteMjTask(Long id);

}
