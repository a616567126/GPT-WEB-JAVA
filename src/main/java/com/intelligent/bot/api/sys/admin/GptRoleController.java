package com.intelligent.bot.api.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.GptRoleAddReq;
import com.intelligent.bot.model.req.sys.admin.GptRoleQueryReq;
import com.intelligent.bot.model.req.sys.admin.GptRoleUpdateReq;
import com.intelligent.bot.model.res.sys.admin.GptRoleQueryRes;
import com.intelligent.bot.service.sys.IGptRoleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/sys/gpt/role")
@Log4j2
public class GptRoleController {



    @Resource
    IGptRoleService gptRoleService;


    @RequestMapping(value = "/queryPage",name = "查询gpt角色", method = RequestMethod.POST)
    public B<Page<GptRoleQueryRes>> queryPage(@Validated @RequestBody GptRoleQueryReq req) {
        return gptRoleService.queryPage(req);
    }

    @RequestMapping(value = "/add",name = "新增gpt角色", method = RequestMethod.POST)
    public B<Void> add(@Validated @RequestBody GptRoleAddReq req) {
        return gptRoleService.add(req);
    }

    @RequestMapping(value = "/update",name = "修改gpt角色", method = RequestMethod.POST)
    public B update(@Validated @RequestBody GptRoleUpdateReq req) {
        return gptRoleService.update(req);
    }

    @RequestMapping(value = "/delete",name = "删除gpt角色", method = RequestMethod.POST)
    public B<Void> delete(@Validated @RequestBody BaseDeleteEntity req) {
        return gptRoleService.delete(req);
    }

}
