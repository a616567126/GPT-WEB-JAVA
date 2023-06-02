package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.dao.SysConfigDao;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.model.req.sys.admin.SysConfigUpdateReq;
import com.intelligent.bot.model.res.sys.admin.SysConfigQueryRes;
import com.intelligent.bot.service.sys.ISysConfigService;
import com.intelligent.bot.utils.sys.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;


@Service("SysConfigService")
@Transactional(rollbackFor = Exception.class)
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfig> implements ISysConfigService {

    @Resource
    RedisUtil redisUtil;

    @Override
    public B<SysConfigQueryRes> queryPage() {
        SysConfig sysConfig = this.lambdaQuery().eq(SysConfig::getId, 1).one();
        return B.okBuild(BeanUtil.copyProperties(sysConfig, SysConfigQueryRes.class));
    }

    @Override
    public B<Void> update(SysConfigUpdateReq req) {
        SysConfig sysConfig = BeanUtil.copyProperties(req, SysConfig.class);
        sysConfig.setOperateTime(LocalDateTime.now());
        this.updateById(sysConfig);
        redisUtil.setCacheObject(CommonConst.SYS_CONFIG,this.getById(sysConfig.getId()));
        return B.okBuild();
    }
}
