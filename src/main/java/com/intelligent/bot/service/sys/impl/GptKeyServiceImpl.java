package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.dao.GptKeyDao;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.model.GptKey;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.req.sys.admin.GptKeyAddReq;
import com.intelligent.bot.model.req.sys.admin.GptKeyUpdateStateReq;
import com.intelligent.bot.model.res.sys.admin.GptKeyQueryRes;
import com.intelligent.bot.service.sys.IGptKeyService;
import com.intelligent.bot.utils.sys.InitUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service("GptKeyService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class GptKeyServiceImpl extends ServiceImpl<GptKeyDao, GptKey> implements IGptKeyService {


    @Override
    public B<Page<GptKeyQueryRes>> queryPage(BasePageHelper basePageHelper) {
        Page<GptKeyQueryRes> page = new Page<>(basePageHelper.getPageNumber(),basePageHelper.getPageSize());
        return B.okBuild(this.baseMapper.queryGptKey(page));
    }

    @Override
    public B<Void> add(GptKeyAddReq req) {
        GptKey gptKey = BeanUtil.copyProperties(req, GptKey.class);
        Long count = this.lambdaQuery()
                .eq(null != gptKey.getKey(), GptKey::getKey,gptKey.getKey())
                .count();
        if(count > 0){
            return B.finalBuild("key已存在");
        }
        gptKey.setCreateTime(LocalDateTime.now());
        gptKey.setOperateTime(LocalDateTime.now());
        InitUtil.add(gptKey.getKey());
        log.error("新增key：{}======缓存key信息：{}",gptKey.getKey(), InitUtil.getAllKey());
        this.save(gptKey);
        return B.okBuild();
    }

    @Override
    public B<Void> delete(BaseDeleteEntity req) {
        List<GptKey> list = this.lambdaQuery().in(GptKey::getId, req.getIds()).list();
        this.removeByIds(req.getIds());
        list.forEach( l ->{
            InitUtil.removeKey(l.getKey());
        });
        log.error("删除：{}======缓存key信息：{}",list, InitUtil.getAllKey());
        return B.okBuild();
    }

    @Override
    public B<Void> updateState(GptKeyUpdateStateReq req) {
        GptKey gptKey = this.getById(req.getId());
        if(null == gptKey){
            throw new E("key不存在");
        }
        gptKey.setState(req.getState());
        InitUtil.add(gptKey.getKey());
        this.saveOrUpdate(gptKey);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }
}
