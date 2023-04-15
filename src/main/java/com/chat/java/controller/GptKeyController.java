package com.chat.java.controller;

import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.GptKeyAddReq;
import com.chat.java.model.req.UpdateKeyStateReq;
import com.chat.java.service.IGptKeyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/gpt/key")
@RequiredArgsConstructor
@Api(tags = {"gptkey管理"})
public class GptKeyController {



    final IGptKeyService gptKeyService;


    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询gptKey")
    public B<JSONObject> queryPage(@Validated @RequestBody BasePageHelper basePageHelper) {
        return gptKeyService.queryPage(basePageHelper);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增gptKey")
    public B<Void> add(@Validated @RequestBody GptKeyAddReq req) {
        return gptKeyService.add(req);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除gptKey")
    public B delete(@Validated @RequestBody BaseDeleteEntity params) {
        return gptKeyService.delete(params);
    }

    @RequestMapping(value = "/updateState", method = RequestMethod.POST)
    @ApiOperation(value = "修改key状态")
    public B updateState(@Validated @RequestBody UpdateKeyStateReq params) {
        return gptKeyService.updateState(params);
    }

}
