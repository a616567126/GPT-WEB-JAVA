package com.intelligent.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.dao.MjTaskDao;
import com.intelligent.bot.model.MjTask;
import com.intelligent.bot.service.sys.IMjTaskService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("MjTaskService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class MjTaskImpl extends ServiceImpl<MjTaskDao, MjTask> implements IMjTaskService {


}
