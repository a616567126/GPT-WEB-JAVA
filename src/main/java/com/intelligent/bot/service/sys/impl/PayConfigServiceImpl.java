package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.dao.PayConfigDao;
import com.intelligent.bot.model.PayConfig;
import com.intelligent.bot.model.req.sys.admin.PayConfigUpdateReq;
import com.intelligent.bot.model.res.sys.admin.PayConfigQueryRes;
import com.intelligent.bot.service.sys.IPayConfigService;
import com.intelligent.bot.utils.sys.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;


@Service("PayConfigService")
@Transactional(rollbackFor = Exception.class)
public class PayConfigServiceImpl extends ServiceImpl<PayConfigDao, PayConfig> implements IPayConfigService {

    @Resource
    RedisUtil redisUtil;

    @Override
    public B<PayConfigQueryRes> queryPayConfig() {
        PayConfig payConfig = this.lambdaQuery().eq(PayConfig::getId, 1).one();
        return B.okBuild(BeanUtil.copyProperties(payConfig,PayConfigQueryRes.class));
    }

    @Override
    public B<Void> update(PayConfigUpdateReq req) {
        PayConfig payConfig = BeanUtil.copyProperties(req,PayConfig.class);
        payConfig.setOperateTime(LocalDateTime.now());
        this.updateById(payConfig);
        redisUtil.setCacheObject(CommonConst.PAY_CONFIG,this.getById(payConfig.getId()));
        return B.okBuild();
    }
}
