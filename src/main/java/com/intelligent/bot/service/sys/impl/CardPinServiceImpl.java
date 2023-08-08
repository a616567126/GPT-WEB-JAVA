package com.intelligent.bot.service.sys.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.dao.CardPinDao;
import com.intelligent.bot.model.CardPin;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.req.sys.admin.CardPinAddReq;
import com.intelligent.bot.model.res.sys.admin.CardPinQueryRes;
import com.intelligent.bot.service.sys.ICardPinService;
import com.intelligent.bot.utils.sys.PasswordUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service("CardPinService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class CardPinServiceImpl extends ServiceImpl<CardPinDao, CardPin> implements ICardPinService {


    @Override
    public B<Page<CardPinQueryRes>> queryPage(BasePageHelper req) {
        Page<CardPinQueryRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        return B.okBuild(this.baseMapper.queryCardPin(page));
    }

    @Override
    public B<Void> add(CardPinAddReq req) {
        CardPin cardPin = null;
        List<CardPin> list = new ArrayList<>();
        for (int i = 0; i < req.getBatch(); i++) {
            cardPin = new CardPin();
            cardPin.setNumber(req.getNumber());
            cardPin.setCardPin(PasswordUtil.getRandomPassword());
            list.add(cardPin);
        }
        if(list.size() > 0){
            this.saveBatch(list);
        }
        return B.okBuild();
    }

    @Override
    public B<Void> delete(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return B.okBuild();
    }

    @Override
    public B<List<String>> getAllCardPin() {
        List<CardPin> list = this.lambdaQuery().eq(CardPin::getState, 0).select(CardPin::getCardPin).list();
        List<String> cardPinList = null == list ? new ArrayList<>() : list.stream().map(CardPin::getCardPin).collect(Collectors.toList());
        return B.okBuild(cardPinList);
    }

    @Override
    public String add(Integer number) {
        CardPin cardPin = new CardPin();
        cardPin.setNumber(number);
        cardPin.setCardPin(PasswordUtil.getRandomPassword());
        this.saveOrUpdate(cardPin);
        return cardPin.getCardPin();
    }

    @Override
    public int checkUseBatchCardPin(Long userId, String cardPin) {
        return baseMapper.checkUseBatchCardPin(userId,cardPin);
    }
}
