package com.intelligent.bot.api.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.DiscordAccountConfig;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.req.sys.admin.DiscordAccountConfigAdd;
import com.intelligent.bot.model.req.sys.admin.DiscordAccountConfigUpdate;
import com.intelligent.bot.service.sys.IDiscordAccountConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys/discord/account/config")
public class DiscordAccountConfigController {

    @Resource
    IDiscordAccountConfigService discordAccountConfigService;


    @RequestMapping(value = "/queryPage",name = "查询", method = RequestMethod.POST)
    public B<Page<DiscordAccountConfig>> queryPage(@Validated @RequestBody BasePageHelper req) {
        return discordAccountConfigService.queryPage(req);
    }

    @RequestMapping(value = "/add",name = "新增", method = RequestMethod.POST)
    public B<String> add(@Validated @RequestBody DiscordAccountConfigAdd req) {
        return discordAccountConfigService.add(req);
    }

    @RequestMapping(value = "/update",name = "编辑", method = RequestMethod.POST)
    public B<String> update(@Validated @RequestBody DiscordAccountConfigUpdate req) {
        return discordAccountConfigService.update(req);
    }

//    @RequestMapping(value = "/delete",name = "删除", method = RequestMethod.POST)
//    public B<Void> delete(@Validated @RequestBody BaseDeleteEntity req) {
//        return discordAccountConfigService.delete(req);
//    }



}
