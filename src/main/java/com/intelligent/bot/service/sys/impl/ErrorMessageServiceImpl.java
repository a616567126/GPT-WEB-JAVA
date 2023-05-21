package com.intelligent.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.dao.ErrorMessageDao;
import com.intelligent.bot.model.ErrorMessage;
import com.intelligent.bot.service.sys.IErrorMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("ErrorMessageService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class ErrorMessageServiceImpl extends ServiceImpl<ErrorMessageDao, ErrorMessage> implements IErrorMessageService {



}
