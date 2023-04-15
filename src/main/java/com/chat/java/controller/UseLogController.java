package com.chat.java.controller;

import com.chat.java.base.B;
import com.chat.java.model.req.ResetLogReq;
import com.chat.java.model.req.UpdateLogReq;
import com.chat.java.service.IUseLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品表(Product)表控制层
 *
 * @author  
 * @since 2022-03-12 15:23:19
 */
@RestController
@RequestMapping("/use/log")
@RequiredArgsConstructor
@Api(tags = {"对话管理"})
public class UseLogController {


    /**
     * ProductService
     */
    final IUseLogService useLogService;


    @RequestMapping(value = "/updateLog", method = RequestMethod.POST)
    @ApiOperation(value = "修改聊天记录")
    public B updateLog(@Validated @RequestBody UpdateLogReq req) {
        return useLogService.updateLog(req);
    }
    @RequestMapping(value = "/resetLog", method = RequestMethod.POST)
    @ApiOperation(value = "修改聊天状态")
    public B resetLog(@Validated @RequestBody ResetLogReq req) {
        return useLogService.resetLog(req);
    }

}
