package com.cn.app.chatgptbot.service.impl;

import cn.hutool.core.exceptions.ValidateException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.dao.AnnouncementDao;
import com.cn.app.chatgptbot.dao.ProductDao;
import com.cn.app.chatgptbot.model.Announcement;
import com.cn.app.chatgptbot.model.Product;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.service.IAnnouncementService;
import com.cn.app.chatgptbot.service.IProductService;
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
    public B queryPage(BasePageHelper basePageHelper) {
        JSONObject jsonObject = new JSONObject();
        Page<Announcement> page = new Page<>(basePageHelper.getPageNumber(),basePageHelper.getPageSize());
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper();
        queryWrapper.like(null != basePageHelper.getType(),"type",basePageHelper.getType());
        queryWrapper.orderByDesc("sort");
        Page<Announcement> productPage = baseMapper.selectPage(page, queryWrapper);
        jsonObject.put("productPage",productPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }


    @Override
    public B add(String params) {
        Announcement announcement = JSONObject.parseObject(params, Announcement.class);
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
    public B update(String params) {
        Announcement announcement = JSONObject.parseObject(params, Announcement.class);
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
    public B delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

}
