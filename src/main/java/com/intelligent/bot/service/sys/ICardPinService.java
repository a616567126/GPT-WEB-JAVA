package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.CardPin;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.req.sys.admin.CardPinAddReq;
import com.intelligent.bot.model.res.sys.admin.CardPinQueryRes;

import java.util.List;


public interface ICardPinService extends IService<CardPin> {


    B<Page<CardPinQueryRes>> queryPage(BasePageHelper req);


    B<Void> add(CardPinAddReq req);


    B<Void> delete(BaseDeleteEntity req);

    B<List<String>> getAllCardPin();

    String add(Integer number);

    int checkUseBatchCardPin(Long userId, String cardPin);


}
