package com.intelligent.bot.utils.sys;


import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.*;
import com.intelligent.bot.service.sys.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class InitUtil {

    @Resource
    IGptKeyService gptKeyService;

    @Resource
    AsyncService asyncService;
    @Resource
    RedisUtil redisUtil;
    @Resource
    ISysConfigService sysConfigService;
    @Resource
    IPayConfigService payConfigService;
    @Resource
    IEmailService emailService;
    @Resource
    IDiscordAccountConfigService discordAccountConfigService;


    private static InitUtil initUtil;



    /**
     * key缓存池
     */
    private static final ConcurrentHashMap<String, GptKey> cache = new ConcurrentHashMap<>();


    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        //获取启用的key
        List<GptKey> gptKeyList = gptKeyService.lambdaQuery().eq(GptKey::getState,0).orderByDesc(GptKey::getSort).list();
//        gptKeyList.stream().map(GptKey::getKey).collect(Collectors.toList()).forEach(InitUtil::add);
        gptKeyList.forEach( g ->{
            InitUtil.add(g.getId().toString(),g);
        });
        initUtil = this;
        initUtil.gptKeyService = this.gptKeyService;
        initUtil.asyncService = this.asyncService;
        SysConfig sysConfig = sysConfigService.getById(1);
        PayConfig payConfig = payConfigService.getById(1);
        redisUtil.setCacheObject(CommonConst.SYS_CONFIG,sysConfig);
        redisUtil.setCacheObject(CommonConst.PAY_CONFIG,payConfig);
        if(sysConfig.getRegistrationMethod() == 2){
            List<EmailConfig> emailConfigList = emailService.list();
            if(null != emailConfigList && !emailConfigList.isEmpty()){
                redisUtil.setCacheObject(CommonConst.EMAIL_LIST,emailConfigList);
            }
        }
        if(sysConfig.getIsOpenProxy() == 1){
            System.setProperty("http.proxyHost", sysConfig.getProxyIp());
            System.setProperty("http.proxyPort", String.valueOf(sysConfig.getProxyPort()));
            System.setProperty("https.proxyHost", sysConfig.getProxyIp());
            System.setProperty("https.proxyPort", String.valueOf(sysConfig.getProxyPort()));
        }
        //加载mj账号池
        if(sysConfig.getIsOpenMj() == 1){
            if(sysConfig.getIsOpenProxy() == 1){
                System.setProperty("http.proxyHost", sysConfig.getProxyIp());
                System.setProperty("http.proxyPort", String.valueOf(sysConfig.getProxyPort()));
                System.setProperty("https.proxyHost", sysConfig.getProxyIp());
                System.setProperty("https.proxyPort", String.valueOf(sysConfig.getProxyPort()));
            }
            List<DiscordAccountConfig> discordAccountConfigList = discordAccountConfigService.lambdaQuery().eq(DiscordAccountConfig::getState, 1).list();
            for (DiscordAccountConfig configAccount : discordAccountConfigList) {
                discordAccountConfigService.addAccount(configAccount);
            }
        }
    }

    /**
     * 添加key到缓存
     * @param str
     */
    public static void add(String str,GptKey gptKey) {
        cache.putIfAbsent(str, gptKey);
    }



    /**
     * 获取全部的key
     * @return
     */
    public static Collection<GptKey> getAllKey() {
        return cache.values();
    }


    public static synchronized String getRandomKey(Integer type) {
        final Collection<GptKey> allKey = getAllKey();
        if (CollectionUtils.isEmpty(allKey)) {
            throw new E("缓存池中已无可用的Key 请联系管理员");
        }
        final List<String> list = new ArrayList<>();
        allKey.forEach( key ->{
            if(key.getType().equals(type)){
                list.add(key.getKey());
            }
        });
        if (CollectionUtils.isEmpty(list)) {
            throw new E("缓存池中已无可用的Key 请联系管理员");
        }
        Collections.shuffle(list);
        String key = list.get(0);
        initUtil.asyncService.updateKeyNumber(key);
        return key;
    }

    public static void removeKey(final String key) {
        cache.remove(key);
    }

}
