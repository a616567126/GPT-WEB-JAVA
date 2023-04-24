package com.chat.java.controller;

import com.chat.java.base.B;
import com.chat.java.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sys/config")
@RequiredArgsConstructor
@Api(tags = {"系统配置管理"})
public class SysConfigController {


    /**
     * gptKeyService
     */
    final ISysConfigService payConfigService;


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
