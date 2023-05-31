package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.ErrorMessage;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.res.sys.admin.ErrorMessageQueryRes;


public interface IErrorMessageService extends IService<ErrorMessage> {

    B<Page<ErrorMessageQueryRes>> queryPage(BasePageHelper req);



}
