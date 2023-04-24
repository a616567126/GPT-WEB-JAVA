package com.chat.java.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.base.B;
import com.chat.java.base.ResultEnum;
import com.chat.java.dao.EmailConfigDao;
import com.chat.java.exception.CustomException;
import com.chat.java.model.EmailConfig;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.EmailConfigAddReq;
import com.chat.java.model.req.EmailConfigPageReq;
import com.chat.java.model.req.EmailConfigUpdateReq;
import com.chat.java.service.IEmailService;
import com.chat.java.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service("EmailService")
@Log4j2
@Transactional(rollbackFor = Exception.class)
public class EmailServiceImpl extends ServiceImpl<EmailConfigDao, EmailConfig> implements IEmailService {

    @Resource
    RedisUtil redisUtil;

    @Override
    public B<JSONObject> queryPage(EmailConfigPageReq req) {
        JSONObject jsonObject = new JSONObject();
        Page<EmailConfig> page = new Page<>(req.getPageNumber(),req.getPageSize());
        QueryWrapper<EmailConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.like(null != req.getUsername(),"username",req.getUsername());
        Page<EmailConfig> productPage = baseMapper.selectPage(page, queryWrapper);
        jsonObject.put("emailPage",productPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }

    @Override
    public B<Void> add(EmailConfigAddReq req) {
        EmailConfig emailConfig = BeanUtil.copyProperties(req, EmailConfig.class);
        Long count = this.lambdaQuery().eq(EmailConfig::getUsername, emailConfig.getUsername()).count();
        if(count > 0){
            throw new CustomException("邮箱已存在");
        }
        this.save(emailConfig);
        List<EmailConfig> emailConfigList = this.list();
        redisUtil.setCacheObject("emailList",emailConfigList);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    @Override
    public B<Void> update(EmailConfigUpdateReq req) {
        EmailConfig emailConfig = BeanUtil.copyProperties(req, EmailConfig.class);
        Long count = this.lambdaQuery().
                eq(EmailConfig::getUsername, emailConfig.getUsername())
                .ne(EmailConfig::getId, emailConfig.getId())
                .count();
        if(count > 0){
            throw new CustomException("邮箱已存在");
        }
        this.saveOrUpdate(emailConfig);
        List<EmailConfig> emailConfigList = this.list();
        redisUtil.setCacheObject("emailList",emailConfigList);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    @Override
    public B<Void> delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        List<EmailConfig> emailConfigList = this.list();
        redisUtil.setCacheObject("emailList",emailConfigList);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }
}
