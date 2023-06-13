package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.dao.SdLoraDao;
import com.intelligent.bot.model.SdLora;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.SdLoraAddReq;
import com.intelligent.bot.model.req.sys.admin.SdLoraQueryReq;
import com.intelligent.bot.model.req.sys.admin.SdLoraUpdateReq;
import com.intelligent.bot.model.res.sys.admin.SdLoraQueryRes;
import com.intelligent.bot.service.sys.ISdLoraService;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("SdLoraService")
@Log4j2
@Transactional(rollbackFor = E.class)
public class SdLoraServiceImpl extends ServiceImpl<SdLoraDao, SdLora> implements ISdLoraService {


    @Override
    public B<Page<SdLoraQueryRes>> queryLoraPage(SdLoraQueryReq req) {
        Page<SdLoraQueryRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        Page<SdLoraQueryRes> sdLoraQueryResPage = this.baseMapper.queryLoraPage(page, req);
        SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
        sdLoraQueryResPage.getRecords().forEach(s ->{
            s.setImgReturnUrl(cacheObject.getImgReturnUrl());
        });
        return B.okBuild(sdLoraQueryResPage);
    }

    @Override
    public B<Void> addLora(SdLoraAddReq req) {
        Long count = this.lambdaQuery().eq(SdLora::getLoraName, req.getLoraName()).count();
        if(count > 0){
            throw new E("Lora已存在");
        }
        SdLora sdLora = BeanUtil.copyProperties(req, SdLora.class);
        this.save(sdLora);
        return B.okBuild();
    }

    @Override
    public B<Void> updateLora(SdLoraUpdateReq req) {
        Long count = this.lambdaQuery()
                .eq(SdLora::getLoraName, req.getLoraName())
                .ne(SdLora::getId,req.getId())
                .count();
        if(count > 0){
            throw new E("Lora已存在");
        }
        SdLora sdLora = BeanUtil.copyProperties(req, SdLora.class);
        this.updateById(sdLora);
        return B.okBuild();
    }

    @Override
    public B<Void> deleteLora(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return B.okBuild();
    }
}
