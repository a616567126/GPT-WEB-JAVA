package com.intelligent.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.EmailConfig;
import com.intelligent.bot.model.res.sys.admin.EmailConfigQueryRes;
import org.apache.ibatis.annotations.Param;


public interface EmailConfigDao extends BaseMapper<EmailConfig> {

    Page<EmailConfigQueryRes> queryEmailConfig(Page<EmailConfigQueryRes> page, @Param("username") String username);

}


