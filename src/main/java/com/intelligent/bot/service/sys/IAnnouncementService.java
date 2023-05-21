package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.Announcement;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.AnnouncementAddReq;
import com.intelligent.bot.model.req.sys.admin.AnnouncementQueryReq;
import com.intelligent.bot.model.req.sys.admin.AnnouncementUpdateReq;
import com.intelligent.bot.model.res.sys.admin.AnnouncementQueryRes;


public interface IAnnouncementService extends IService<Announcement> {


    B<Page<AnnouncementQueryRes>> queryPage(AnnouncementQueryReq req);


    B<Void> add(AnnouncementAddReq req);


    B<Void>  update(AnnouncementUpdateReq req);


    B<Void>  delete(BaseDeleteEntity params);



}
