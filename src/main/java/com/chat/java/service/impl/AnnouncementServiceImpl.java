package com.chat.java.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.model.Announcement;
import com.chat.java.base.B;
import com.chat.java.base.ResultEnum;
import com.chat.java.dao.AnnouncementDao;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.AnnouncementAddReq;
import com.chat.java.model.req.AnnouncementPageReq;
import com.chat.java.model.req.AnnouncementUpdateReq;
import com.chat.java.service.IAnnouncementService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户表(Product)表服务实现类
 *
 * @author  
 * @since 2022-03-12 15:23:55
 */
@Service("AnnouncementService")
@Transactional(rollbackFor = Exception.class)
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementDao, Announcement> implements IAnnouncementService {




    @Override
    public B<JSONObject> queryPage(AnnouncementPageReq req) {
        JSONObject jsonObject = new JSONObject();
        Page<Announcement> page = new Page<>(req.getPageNumber(),req.getPageSize());
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(null != req.getType(),"type",req.getType());
        queryWrapper.orderByDesc("sort");
        Page<Announcement> productPage = baseMapper.selectPage(page, queryWrapper);
        jsonObject.put("productPage",productPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }


    @Override
    public B<Void> add(AnnouncementAddReq req) {
        Announcement announcement = BeanUtil.copyProperties(req,Announcement.class);
        Long count = this.lambdaQuery()
                .eq(null != announcement.getTitle(), Announcement::getTitle,announcement.getTitle())
                .eq(null != announcement.getType(), Announcement::getType,announcement.getType())
                .count();
        if(count > 0){
            return B.finalBuild("公告/指南已存在");
        }
        this.save(announcement);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }


    @Override
    public B<Void> update(AnnouncementUpdateReq req) {
        Announcement announcement = BeanUtil.copyProperties(req,Announcement.class);
        if (null == announcement || null == announcement.getId()) {
            return B.finalBuild("参数校验失败");
        }
        Long count = this.lambdaQuery()
                .eq(null != announcement.getTitle(), Announcement::getTitle,announcement.getTitle())
                .eq(null != announcement.getType(), Announcement::getType,announcement.getType())
                .ne(Announcement::getId,announcement.getId())
                .count();
        if(count > 0){
            return B.finalBuild("公告/指南已存在");
        }
        this.saveOrUpdate(announcement);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }


    @Override
    public B<Void> delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

}
