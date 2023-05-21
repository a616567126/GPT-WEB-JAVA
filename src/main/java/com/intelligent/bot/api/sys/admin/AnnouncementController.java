 package com.intelligent.bot.api.sys.admin;

 import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
 import com.intelligent.bot.base.result.B;
 import com.intelligent.bot.model.base.BaseDeleteEntity;
 import com.intelligent.bot.model.req.sys.admin.AnnouncementAddReq;
 import com.intelligent.bot.model.req.sys.admin.AnnouncementQueryReq;
 import com.intelligent.bot.model.req.sys.admin.AnnouncementUpdateReq;
 import com.intelligent.bot.model.res.sys.admin.AnnouncementQueryRes;
 import com.intelligent.bot.service.sys.IAnnouncementService;
 import org.springframework.validation.annotation.Validated;
 import org.springframework.web.bind.annotation.RequestBody;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RequestMethod;
 import org.springframework.web.bind.annotation.RestController;

 import javax.annotation.Resource;


 @RestController
@RequestMapping("/sys/announcement")
public class AnnouncementController {


   @Resource
   IAnnouncementService announcementService;


    @RequestMapping(value = "/queryPage",name = "查询公告列表分页", method = RequestMethod.POST)
    public B<Page<AnnouncementQueryRes>>  queryPage(@Validated @RequestBody AnnouncementQueryReq req) {
        return announcementService.queryPage(req);
    }

    @RequestMapping(value = "/add",name = "新增公告", method = RequestMethod.POST)
    public B<Void>  add(@Validated @RequestBody AnnouncementAddReq req) {
        return announcementService.add(req);
    }


    @RequestMapping(value = "/update",name = "编辑公告", method = RequestMethod.POST)
    public B<Void>  update(@Validated @RequestBody AnnouncementUpdateReq req) {
        return announcementService.update(req);
    }

    @RequestMapping(value = "/delete",name = "删除公告", method = RequestMethod.POST)
    public B<Void>  delete(@Validated @RequestBody BaseDeleteEntity params) {
        return announcementService.delete(params);
    }

}
