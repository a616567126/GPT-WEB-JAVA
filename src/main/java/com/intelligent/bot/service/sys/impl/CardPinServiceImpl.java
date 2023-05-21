package com.intelligent.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.dao.CardPinDao;
import com.intelligent.bot.model.CardPin;
import com.intelligent.bot.service.sys.ICardPinService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("CardPinService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class CardPinServiceImpl extends ServiceImpl<CardPinDao, CardPin> implements ICardPinService {

}
