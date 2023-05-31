package com.intelligent.bot.api.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.req.sys.admin.CardPinAddReq;
import com.intelligent.bot.model.res.sys.admin.CardPinQueryRes;
import com.intelligent.bot.service.sys.ICardPinService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/sys/card/pin")
public class CardPinController {


    @Resource
    ICardPinService cardPinService;

    @RequestMapping(value = "/queryPage",name = "查询卡密", method = RequestMethod.POST)
    public B<Page<CardPinQueryRes>> queryPage(@Validated @RequestBody BasePageHelper req) {
        return cardPinService.queryPage(req);
    }

    @RequestMapping(value = "/add",name = "新增卡密", method = RequestMethod.POST)
    public B<Void> add(@Validated @RequestBody CardPinAddReq req) {
        return cardPinService.add(req);
    }



    @RequestMapping(value = "/delete",name = "删除卡密", method = RequestMethod.POST)
    public B<Void> delete(@Validated @RequestBody BaseDeleteEntity req) {
        return cardPinService.delete(req);
    }

    @RequestMapping(value = "/getAllCardPin",name = "获取有效的卡密列表", method = RequestMethod.POST)
    public B<List<String>> getAllCardPin() {
        return cardPinService.getAllCardPin();
    }

}
