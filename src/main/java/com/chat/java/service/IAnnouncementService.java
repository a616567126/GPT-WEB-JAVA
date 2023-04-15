package com.chat.java.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.java.model.Announcement;
import com.chat.java.base.B;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.AnnouncementAddReq;
import com.chat.java.model.req.AnnouncementPageReq;
import com.chat.java.model.req.AnnouncementUpdateReq;

/**
 * 商品表(Product)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IAnnouncementService extends IService<Announcement> {



    B<JSONObject> queryPage(AnnouncementPageReq req);


    B<Void> add(AnnouncementAddReq req);


    B<Void> update(AnnouncementUpdateReq req);


    B<Void> delete(BaseDeleteEntity params);




}
