package com.intelligent.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.Announcement;
import com.intelligent.bot.model.res.sys.admin.AnnouncementQueryRes;
import org.apache.ibatis.annotations.Param;

public interface AnnouncementDao extends BaseMapper<Announcement> {


    Page<AnnouncementQueryRes> queryAnnouncement(Page<AnnouncementQueryRes> page, @Param("title") String title);
}
