package com.intelligent.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.ErrorMessage;
import com.intelligent.bot.model.res.sys.admin.ErrorMessageQueryRes;

public interface ErrorMessageDao extends BaseMapper<ErrorMessage> {


    Page<ErrorMessageQueryRes> queryErrorMessage(Page<ErrorMessageQueryRes> page);

}
