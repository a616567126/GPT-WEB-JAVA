package com.intelligent.bot.api.sys.admin;

import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.req.sys.admin.SysConfigUpdateReq;
import com.intelligent.bot.model.res.sys.admin.SysConfigQueryRes;
import com.intelligent.bot.service.sys.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/sys/config")
@RequiredArgsConstructor
public class SysConfigController {


    @Resource
    ISysConfigService payConfigService;


    @RequestMapping(value = "/queryPage", name = "查询payConfig",method = RequestMethod.POST)
    public B<SysConfigQueryRes> querySysConfig() {
        return payConfigService.queryPage();
    }


    @RequestMapping(value = "/update",name = "更新payConfig", method = RequestMethod.POST)
    public B<Void> update(@Validated @RequestBody SysConfigUpdateReq req) {
        return payConfigService.update(req);
    }


}
