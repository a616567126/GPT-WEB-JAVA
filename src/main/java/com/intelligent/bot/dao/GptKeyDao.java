package com.intelligent.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.GptKey;
import com.intelligent.bot.model.res.sys.admin.GptKeyQueryRes;


public interface GptKeyDao extends BaseMapper<GptKey> {

    Page<GptKeyQueryRes> queryGptKey(Page<GptKeyQueryRes> page);



}
