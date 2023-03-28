package com.cn.app.chatgptbot.controller;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.service.IGptKeyService;
import com.cn.app.chatgptbot.service.IPayConfigService;
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
@Api(tags = {"payConfig"})
public class PayConfigController {


    /**
     * gptKeyService
     */
    final IPayConfigService payConfigService;


    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "查询payConfig")
    public B queryPage() {
        return payConfigService.queryPage();
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "更新payConfig")
    public B update(@Validated @RequestBody String params) {
        return payConfigService.update(params);
    }


}
