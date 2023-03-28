package com.cn.app.chatgptbot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.dao.GptKeyDao;
import com.cn.app.chatgptbot.dao.PayConfigDao;
import com.cn.app.chatgptbot.model.GptKey;
import com.cn.app.chatgptbot.model.PayConfig;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.service.IGptKeyService;
import com.cn.app.chatgptbot.service.IPayConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户表(Product)表服务实现类
 *
 * @author  
 * @since 2022-03-12 15:23:55
 */
@Service("PayConfigService")
@Transactional(rollbackFor = Exception.class)
public class PayConfigServiceImpl extends ServiceImpl<PayConfigDao, PayConfig> implements IPayConfigService {



    @Override
    public B queryPage() {
        JSONObject jsonObject = new JSONObject();
        PayConfig payConfig = this.lambdaQuery().eq(PayConfig::getId, 1).one();
        jsonObject.put("payConfig",payConfig);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }


    public B update(String params) {
        PayConfig payConfig = JSONObject.parseObject(params, PayConfig.class);
        payConfig.setOperateTime(LocalDateTime.now());
        this.updateById(payConfig);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }



}
