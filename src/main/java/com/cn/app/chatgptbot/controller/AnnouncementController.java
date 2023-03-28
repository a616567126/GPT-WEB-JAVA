package com.cn.app.chatgptbot.controller;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.service.IAnnouncementService;
import com.cn.app.chatgptbot.service.IProductService;
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
@RequestMapping("/announcement")
@RequiredArgsConstructor
@Api(tags = {"公告(Product)"})
public class AnnouncementController {


    /**
     * ProductService
     */
    final IAnnouncementService announcementService;


    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询announcement")
    public B queryPage(@Validated @RequestBody BasePageHelper basePageHelper) {
        return announcementService.queryPage(basePageHelper);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增announcement")
    public B add(@Validated @RequestBody String params) {
        return announcementService.add(params);
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑announcement")
    public B update(@Validated @RequestBody String params) {
        return announcementService.update(params);
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除announcement")
    public B delete(@Validated @RequestBody BaseDeleteEntity params) {
        return announcementService.delete(params);
    }

}
