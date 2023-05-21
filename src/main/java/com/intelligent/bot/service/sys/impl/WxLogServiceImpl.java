package com.intelligent.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.dao.WxLogDao;
import com.intelligent.bot.model.WxLog;
import com.intelligent.bot.service.sys.IWxLogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Service("WxLogService")
@Log4j2
public class WxLogServiceImpl extends ServiceImpl<WxLogDao, WxLog> implements IWxLogService {

}
