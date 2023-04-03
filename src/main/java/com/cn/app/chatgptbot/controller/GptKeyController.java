package com.cn.app.chatgptbot.controller;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.service.IGptKeyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品表(gptKey)表控制层
 *
 * @author  
 * @since 2022-03-12 15:23:19
 */
@RestController
@RequestMapping("/gpt/key")
@RequiredArgsConstructor
@Api(tags = {"gptkey管理"})
public class GptKeyController {


    /**
     * gptKeyService
     */
    final IGptKeyService gptKeyService;

    /**
     * 分页查询gptKey
     *
     * @param basePageHelper 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询gptKey")
    public B queryPage(@Validated @RequestBody BasePageHelper basePageHelper) {
        return gptKeyService.queryPage(basePageHelper);
    }

    /**
     * 新增gptKey
     *
     * @param params 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增gptKey")
    public B add(@Validated @RequestBody String params) {
        return gptKeyService.add(params);
    }

    /**
     * 通过id删除gptKey
     *
     * @param params 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除gptKey")
    public B delete(@Validated @RequestBody BaseDeleteEntity params) {
        return gptKeyService.delete(params);
    }

}
