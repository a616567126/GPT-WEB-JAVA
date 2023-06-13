package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.dao.SdModelDao;
import com.intelligent.bot.model.SdModel;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.SdModelAddReq;
import com.intelligent.bot.model.req.sys.admin.SdModelQueryReq;
import com.intelligent.bot.model.req.sys.admin.SdModelUpdateReq;
import com.intelligent.bot.model.res.sys.admin.SdModelQueryRes;
import com.intelligent.bot.service.sys.ISdModelService;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("SdModelService")
@Log4j2
@Transactional(rollbackFor = E.class)
public class SdModelServiceImpl extends ServiceImpl<SdModelDao, SdModel> implements ISdModelService {

    @Override
    public B<Page<SdModelQueryRes>> queryModelPage(SdModelQueryReq req) {
        Page<SdModelQueryRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        Page<SdModelQueryRes> sdModelQueryResPage = this.baseMapper.queryModelPage(page, req);
        sdModelQueryResPage.getRecords().forEach( s ->{
            s.setImgReturnUrl(cacheObject.getImgReturnUrl());
        });
        return B.okBuild(sdModelQueryResPage);
    }

    @Override
    public B<Void> addModel(SdModelAddReq req) {
        Long count = this.lambdaQuery().eq(SdModel::getModelName, req.getModelName()).count();
        if(count > 0){
            throw new E("模型已存在");
        }
        SdModel sdModel = BeanUtil.copyProperties(req, SdModel.class);
        this.save(sdModel);
        return B.okBuild();
    }

    @Override
    public B<Void> updateModel(SdModelUpdateReq req) {
        Long count = this.lambdaQuery()
                .eq(SdModel::getModelName, req.getModelName())
                .ne(SdModel::getId,req.getId())
                .count();
        if(count > 0){
            throw new E("模型已存在");
        }
        SdModel sdModel = BeanUtil.copyProperties(req, SdModel.class);
        this.updateById(sdModel);
        return B.okBuild();
    }

    @Override
    public B<Void> deleteModel(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return B.okBuild();
    }
}
