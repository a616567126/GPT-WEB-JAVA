package com.intelligent.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.dao.ErrorMessageDao;
import com.intelligent.bot.model.ErrorMessage;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.res.sys.admin.ErrorMessageQueryRes;
import com.intelligent.bot.service.sys.IErrorMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("ErrorMessageService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class ErrorMessageServiceImpl extends ServiceImpl<ErrorMessageDao, ErrorMessage> implements IErrorMessageService {


    @Override
    public B<Page<ErrorMessageQueryRes>> queryPage(BasePageHelper req) {
        Page<ErrorMessageQueryRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        return B.okBuild(this.baseMapper.queryErrorMessage(page));
    }
}
