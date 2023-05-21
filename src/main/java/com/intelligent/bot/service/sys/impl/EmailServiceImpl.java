package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.dao.EmailConfigDao;
import com.intelligent.bot.model.EmailConfig;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.EmailConfigAddReq;
import com.intelligent.bot.model.req.sys.admin.EmailConfigQueryReq;
import com.intelligent.bot.model.req.sys.admin.EmailConfigUpdateReq;
import com.intelligent.bot.model.res.sys.admin.EmailConfigQueryRes;
import com.intelligent.bot.service.sys.IEmailService;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
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
    public B<Page<EmailConfigQueryRes>> queryPage(EmailConfigQueryReq req) {
        Page<EmailConfigQueryRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        return B.okBuild(this.baseMapper.queryEmailConfig(page,req.getUsername()));
    }

    @Override
    public B<Void> add(EmailConfigAddReq req) {
        EmailConfig emailConfig = BeanUtil.copyProperties(req, EmailConfig.class);
        Long count = this.lambdaQuery().eq(EmailConfig::getUsername, emailConfig.getUsername()).count();
        if(count > 0){
            throw new E("邮箱已存在");
        }
        this.save(emailConfig);
        List<EmailConfig> emailConfigList = this.list();
        redisUtil.setCacheObject("emailList",emailConfigList);
        return B.okBuild();
    }

    @Override
    public B<Void> update(EmailConfigUpdateReq req) {
        EmailConfig emailConfig = BeanUtil.copyProperties(req, EmailConfig.class);
        Long count = this.lambdaQuery().
                eq(EmailConfig::getUsername, emailConfig.getUsername())
                .ne(EmailConfig::getId, emailConfig.getId())
                .count();
        if(count > 0){
            throw new E("邮箱已存在");
        }
        this.saveOrUpdate(emailConfig);
        List<EmailConfig> emailConfigList = this.list();
        redisUtil.setCacheObject("emailList",emailConfigList);
        return B.okBuild();
    }

    @Override
    public B<Void> delete(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        List<EmailConfig> emailConfigList = this.list();
        redisUtil.setCacheObject("emailList",emailConfigList);
        return B.okBuild();
    }
}
