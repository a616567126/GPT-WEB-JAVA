package com.chat.java.controller;

import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.AnnouncementAddReq;
import com.chat.java.model.req.AnnouncementPageReq;
import com.chat.java.model.req.AnnouncementUpdateReq;
import com.chat.java.service.IAnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
@Api(tags = {"公告管理"})
public class AnnouncementController {


    final IAnnouncementService announcementService;


    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询announcement")
    public B<JSONObject> queryPage(@Validated @RequestBody AnnouncementPageReq req) {
        return announcementService.queryPage(req);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增announcement")
    public B<Void> add(@Validated @RequestBody AnnouncementAddReq req) {
        return announcementService.add(req);
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑announcement")
    public B<Void> update(@Validated @RequestBody AnnouncementUpdateReq req) {
        return announcementService.update(req);
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除announcement")
    public B<Void> delete(@Validated @RequestBody BaseDeleteEntity params) {
        return announcementService.delete(params);
    }

}
