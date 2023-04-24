package com.chat.java.controller;

import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.EmailConfigAddReq;
import com.chat.java.model.req.EmailConfigPageReq;
import com.chat.java.model.req.EmailConfigUpdateReq;
import com.chat.java.service.IEmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/email/config")
@RequiredArgsConstructor
@Api(tags = {"邮件管理"})
public class EmailConfigController {



    final IEmailService emailService;

    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询邮件")
    public B<JSONObject> queryPage(@Validated @RequestBody EmailConfigPageReq req) {
        return emailService.queryPage(req);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增邮件")
    public B<Void> add(@Validated @RequestBody EmailConfigAddReq req) {
        return emailService.add(req);
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑邮件")
    public B<Void> update(@Validated @RequestBody EmailConfigUpdateReq req) {
        return emailService.update(req);
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除邮件")
    public B<Void> delete(@Validated @RequestBody BaseDeleteEntity params) {
        return emailService.delete(params);
    }

}
