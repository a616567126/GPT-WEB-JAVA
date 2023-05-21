package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.dao.AnnouncementDao;
import com.intelligent.bot.model.Announcement;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.AnnouncementAddReq;
import com.intelligent.bot.model.req.sys.admin.AnnouncementQueryReq;
import com.intelligent.bot.model.req.sys.admin.AnnouncementUpdateReq;
import com.intelligent.bot.model.res.sys.admin.AnnouncementQueryRes;
import com.intelligent.bot.service.sys.IAnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("AnnouncementService")
@Transactional(rollbackFor = Exception.class)
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementDao, Announcement> implements IAnnouncementService {


    @Override
    public B<Page<AnnouncementQueryRes>> queryPage(AnnouncementQueryReq req) {
        Page<AnnouncementQueryRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        return B.okBuild( this.baseMapper.queryAnnouncement(page, req.getTitle()));
    }

    @Override
    public B<Void> add(AnnouncementAddReq req) {
        Announcement announcement = BeanUtil.copyProperties(req,Announcement.class);
        Long count = this.lambdaQuery()
                .eq(null != announcement.getTitle(), Announcement::getTitle,announcement.getTitle())
                .count();
        if(count > 0){
            return B.finalBuild("公告已存在");
        }
        this.save(announcement);
        return B.okBuild();
    }


    @Override
    public B<Void> update(AnnouncementUpdateReq req) {
        Announcement announcement = BeanUtil.copyProperties(req,Announcement.class);
        if (null == announcement || null == announcement.getId()) {
            return B.finalBuild("参数校验失败");
        }
        Long count = this.lambdaQuery()
                .eq(null != announcement.getTitle(), Announcement::getTitle,announcement.getTitle())
                .ne(Announcement::getId,announcement.getId())
                .count();
        if(count > 0){
            return B.finalBuild("公告已存在");
        }
        this.saveOrUpdate(announcement);
        return B.okBuild();
    }


    @Override
    public B<Void> delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        return B.okBuild();
    }
}
