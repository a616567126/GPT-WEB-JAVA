package com.intelligent.bot.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.CardPin;
import com.intelligent.bot.model.res.sys.admin.CardPinQueryRes;

public interface CardPinDao extends BaseMapper<CardPin> {

    Page<CardPinQueryRes> queryCardPin(Page<CardPinQueryRes> page);
}
