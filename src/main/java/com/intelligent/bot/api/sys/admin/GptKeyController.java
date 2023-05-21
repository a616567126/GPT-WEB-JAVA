package com.intelligent.bot.api.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.req.sys.admin.GptKeyAddReq;
import com.intelligent.bot.model.req.sys.admin.GptKeyUpdateStateReq;
import com.intelligent.bot.model.res.sys.admin.GptKeyQueryRes;
import com.intelligent.bot.service.sys.IGptKeyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/sys/gpt/key")
public class GptKeyController {



    @Resource
    IGptKeyService gptKeyService;


    @RequestMapping(value = "/queryPage",name = "查询key", method = RequestMethod.POST)
    public B<Page<GptKeyQueryRes>> queryPage(@Validated @RequestBody BasePageHelper basePageHelper) {
        return gptKeyService.queryPage(basePageHelper);
    }


    @RequestMapping(value = "/add",name = "新增key", method = RequestMethod.POST)
    public B<Void> add(@Validated @RequestBody GptKeyAddReq req) {
        return gptKeyService.add(req);
    }


    @RequestMapping(value = "/delete",name = "删除key", method = RequestMethod.POST)
    public B<Void> delete(@Validated @RequestBody BaseDeleteEntity req) {
        return gptKeyService.delete(req);
    }
    @RequestMapping(value = "/updateState",name = "修改key状态", method = RequestMethod.POST)
    public B updateState(@Validated @RequestBody GptKeyUpdateStateReq req) {
        return gptKeyService.updateState(req);
    }

}
