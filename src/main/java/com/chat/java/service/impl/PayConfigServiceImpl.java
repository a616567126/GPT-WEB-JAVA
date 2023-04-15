package com.chat.java.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.base.B;
import com.chat.java.base.ResultEnum;
import com.chat.java.dao.PayConfigDao;
import com.chat.java.model.PayConfig;
import com.chat.java.model.req.PayConfigUpdateReq;
import com.chat.java.service.IPayConfigService;
import com.chat.java.utils.RedisUtil;
import org.springframework.context.annotation.Bean;
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
    public B<JSONObject> queryPage() {
        JSONObject jsonObject = new JSONObject();
        PayConfig payConfig = this.lambdaQuery().eq(PayConfig::getId, 1).one();
        jsonObject.put("payConfig",payConfig);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }


    public B<Void> update(PayConfigUpdateReq req) {
        PayConfig payConfig = BeanUtil.copyProperties(req, PayConfig.class);
        payConfig.setOperateTime(LocalDateTime.now());
        this.updateById(payConfig);
        redisUtil.setCacheObject("payConfig",payConfig);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }



}
