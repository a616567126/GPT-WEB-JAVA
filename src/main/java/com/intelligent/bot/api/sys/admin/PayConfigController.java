package com.intelligent.bot.api.sys.admin;

import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.req.sys.admin.PayConfigUpdateReq;
import com.intelligent.bot.model.res.sys.admin.PayConfigQueryRes;
import com.intelligent.bot.service.sys.IPayConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/sys/pay/config")
public class PayConfigController {


    @Resource
    IPayConfigService payConfigService;


    @RequestMapping(value = "/query",name = "查询payConfig", method = RequestMethod.POST)
    public B<PayConfigQueryRes> queryPayConfig() {
        return payConfigService.queryPayConfig();
    }


    @RequestMapping(value = "/update",name = "更新payConfig", method = RequestMethod.POST)
    public B<Void> update(@Validated @RequestBody PayConfigUpdateReq req) {
        return payConfigService.update(req);
    }


}
