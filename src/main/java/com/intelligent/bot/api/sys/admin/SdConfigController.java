package com.intelligent.bot.api.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.*;
import com.intelligent.bot.model.res.sys.admin.SdLoraQueryRes;
import com.intelligent.bot.model.res.sys.admin.SdModelQueryRes;
import com.intelligent.bot.service.sys.ISdLoraService;
import com.intelligent.bot.service.sys.ISdModelService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/sys/sd/config")
public class SdConfigController {


    @Resource
    ISdModelService sdModelService;

    @Resource
    ISdLoraService sdLoraService;

    @RequestMapping(value = "/query/model/page", name = "查询模型列表",method = RequestMethod.POST)
    public B<Page<SdModelQueryRes>> queryModelPage(@Validated @RequestBody SdModelQueryReq req) {
        return sdModelService.queryModelPage(req);
    }
    @RequestMapping(value = "/add/model", name = "新增模型",method = RequestMethod.POST)
    public B<Void> addModel(@Validated @RequestBody SdModelAddReq req) {
        return sdModelService.addModel(req);
    }

    @RequestMapping(value = "/update/model", name = "编辑模型",method = RequestMethod.POST)
    public B<Void> updateModel(@Validated @RequestBody SdModelUpdateReq req) {
        return sdModelService.updateModel(req);
    }

    @RequestMapping(value = "/delete/model", name = "删除模型",method = RequestMethod.POST)
    public B<Void> deleteModel(@Validated @RequestBody BaseDeleteEntity req) {
        return sdModelService.deleteModel(req);
    }

    @RequestMapping(value = "/query/lora/page", name = "查询lora列表",method = RequestMethod.POST)
    public B<Page<SdLoraQueryRes>> queryLoraPage(@Validated @RequestBody SdLoraQueryReq req) {
        return sdLoraService.queryLoraPage(req);
    }
    @RequestMapping(value = "/add/lora", name = "新增lora",method = RequestMethod.POST)
    public B<Void> addLora(@Validated @RequestBody SdLoraAddReq req) {
        return sdLoraService.addLora(req);
    }

    @RequestMapping(value = "/update/lora", name = "编辑lora",method = RequestMethod.POST)
    public B<Void> updateLora(@Validated @RequestBody SdLoraUpdateReq req) {
        return sdLoraService.updateLora(req);
    }

    @RequestMapping(value = "/delete/lora", name = "删除lora",method = RequestMethod.POST)
    public B<Void> deleteLora(@Validated @RequestBody BaseDeleteEntity req) {
        return sdLoraService.deleteLora(req);
    }
}
