package com.intelligent.bot.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.CardPin;
import com.intelligent.bot.model.res.sys.admin.CardPinQueryRes;
import org.apache.ibatis.annotations.Param;

public interface CardPinDao extends BaseMapper<CardPin> {

    Page<CardPinQueryRes> queryCardPin(Page<CardPinQueryRes> page);

    int checkUseBatchCardPin(@Param("userId") Long userId, @Param("cardPin") String cardPin);
}
