package com.chat.java.controller;

import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.model.req.PayConfigUpdateReq;
import com.chat.java.service.IPayConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/pay/config")
@RequiredArgsConstructor
@Api(tags = {"支付参数管理"})
public class PayConfigController {


    /**
     * gptKeyService
     */
    final IPayConfigService payConfigService;


    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "查询payConfig")
    public B<JSONObject> queryPage() {
        return payConfigService.queryPage();
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "更新payConfig")
    public B<Void> update(@Validated @RequestBody PayConfigUpdateReq req) {
        return payConfigService.update(req);
    }


}
