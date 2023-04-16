package com.chat.java.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.base.B;
import com.chat.java.base.ResultEnum;
import com.chat.java.dao.SysConfigDao;
import com.chat.java.model.SysConfig;
import com.chat.java.service.ISysConfigService;
import com.chat.java.utils.RedisUtil;
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
    public B queryPage() {
        JSONObject jsonObject = new JSONObject();
        SysConfig sysConfig = this.lambdaQuery().eq(SysConfig::getId, 1).one();
        jsonObject.put("sysConfig",sysConfig);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }


    public B update(String params) {
        SysConfig sysConfig = JSONObject.parseObject(params, SysConfig.class);
        sysConfig.setOperateTime(LocalDateTime.now());
        this.updateById(sysConfig);
        redisUtil.setCacheObject("sysConfig",sysConfig);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }



}
