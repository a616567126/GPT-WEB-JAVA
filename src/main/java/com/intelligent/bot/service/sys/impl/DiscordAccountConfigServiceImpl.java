package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ValidateException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.api.midjourney.loadbalancer.DiscordInstance;
import com.intelligent.bot.api.midjourney.loadbalancer.DiscordLoadBalancer;
import com.intelligent.bot.api.midjourney.support.DiscordAccountHelper;
import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.dao.DiscordAccountConfigDao;
import com.intelligent.bot.model.DiscordAccountConfig;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.mj.doman.DiscordAccount;
import com.intelligent.bot.model.mj.doman.ReturnCode;
import com.intelligent.bot.model.req.sys.admin.DiscordAccountConfigAdd;
import com.intelligent.bot.model.req.sys.admin.DiscordAccountConfigUpdate;
import com.intelligent.bot.service.sys.IDiscordAccountConfigService;
import com.intelligent.bot.utils.mj.AsyncLockUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = E.class)
@Log4j2
public class DiscordAccountConfigServiceImpl extends ServiceImpl<DiscordAccountConfigDao, DiscordAccountConfig> implements IDiscordAccountConfigService {

    @Resource
    DiscordAccountHelper discordAccountHelper;

    @Resource
    DiscordLoadBalancer discordLoadBalancer;
    @Override
    public B<Page<DiscordAccountConfig>> queryPage(BasePageHelper req) {
        Page<DiscordAccountConfig> page = new Page<>(req.getPageNumber(),req.getPageSize());
        return B.okBuild(baseMapper.queryPage(page));
    }

    @Override
    public B<String> add(DiscordAccountConfigAdd req) {
        Long count = this.lambdaQuery()
                .eq(DiscordAccountConfig::getChannelId, req.getChannelId())
                .or().eq(DiscordAccountConfig::getGuildId, req.getGuildId())
                .count();
        if(count > 0){
            throw new E("账号信息已存在");
        }
        DiscordAccountConfig discordAccountConfig = BeanUtil.copyProperties(req, DiscordAccountConfig.class);
        this.save(discordAccountConfig);
        DiscordAccount discordAccount = addAccount(discordAccountConfig);
        if(discordAccount.getState() == 1){
            return B.okBuild("账号信息验证成功，已连接");
        }
        return B.okBuild();
    }

    @Override
    public B<String> update(DiscordAccountConfigUpdate req) {
        DiscordAccountConfig oldConfig = this.getById(req.getId());
        DiscordAccountConfig discordAccountConfig = null;
        if(null != req.getChannelId() && null != req.getGuildId()){
            Long count = this.lambdaQuery()
                    .eq( DiscordAccountConfig::getChannelId, req.getChannelId())
                    .or().eq(DiscordAccountConfig::getGuildId, req.getGuildId())
                    .ne(DiscordAccountConfig::getId,req.getId())
                    .count();
            if(count > 0){
                throw new E("账号信息已存在");
            }
            discordAccountConfig = BeanUtil.copyProperties(req, DiscordAccountConfig.class);
        }else {
            discordAccountConfig = oldConfig;
            discordAccountConfig.setState(req.getState());
        }
        this.updateById(discordAccountConfig);
        this.discordLoadBalancer.remove(oldConfig);
        if(req.getState() == 1 ){
            DiscordAccount discordAccount = addAccount(discordAccountConfig);
            if(discordAccount.getState() == 1){
                return B.okBuild("账号信息验证成功，已连接");
            }
        }
        return B.okBuild();
    }

    @Override
    public B<Void> delete(BaseDeleteEntity req) {
        this.removeByIds(req.getIds());
        return B.okBuild();
    }

    @Override
    public DiscordAccount addAccount(DiscordAccountConfig configAccount) {
        DiscordAccount account = new DiscordAccount();
        BeanUtil.copyProperties(configAccount, account);
        account.setId(configAccount.getChannelId());
        DiscordInstance instance = this.discordAccountHelper.createDiscordInstance(account);
        if (null != account.getState() && account.getState() == 1) {
            List<DiscordInstance> instances = this.discordLoadBalancer.getAllInstances();
            try {
                instance.startWss();
                AsyncLockUtils.LockObject lock = AsyncLockUtils.waitForLock("wss:" + account.getChannelId(), Duration.ofSeconds(10));
                if (ReturnCode.SUCCESS != (null == lock.getCode() ? 0 : lock.getCode())) {
                    throw new ValidateException(lock.getDescription());
                }
                instances.add(instance);
            } catch (Exception e) {
                log.error("Account({}) init fail, disabled: {}", account.getDisplay(), e.getMessage());
                account.setState(0);
            }
            Set<String> enableInstanceIds = instances.stream().filter(DiscordInstance::isAlive).map(DiscordInstance::getInstanceId).collect(Collectors.toSet());
            log.info("当前可用账号数 [{}] - {}", enableInstanceIds.size(), String.join(", ", enableInstanceIds));
        }
        return account;
    }
}
