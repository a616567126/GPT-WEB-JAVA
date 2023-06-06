package com.intelligent.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.dao.MjTaskDao;
import com.intelligent.bot.model.MjTask;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.IMjTaskService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service("MjTaskService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class MjTaskImpl extends ServiceImpl<MjTaskDao, MjTask> implements IMjTaskService {

    @Resource
    AsyncService asyncService;
    @Override
    public int emptyMjTask(Long userId) {
        List<MjTask> list = this.lambdaQuery()
                .eq(MjTask::getUserId, userId)
                .list();
        if(null != list && list.size() > 0){
            List<String> imageUrls = list.stream().map(MjTask::getImageUrl).filter(imageUrl -> imageUrl.contains("jpg")).collect(Collectors.toList());
            asyncService.deleteImages(imageUrls);
        }
        return this.baseMapper.batchDeleteByUserId(userId);
    }

    @Override
    public int deleteMjTask(Long id) {
        MjTask mjTask = this.getById(id);
        if(null != mjTask && null != mjTask.getImageUrl()  && mjTask.getImageUrl().contains("jpg")){
            asyncService.deleteImages(Collections.singletonList(mjTask.getImageUrl()));
        }
        return this.baseMapper.deleteByKeyId(id);
    }
}
