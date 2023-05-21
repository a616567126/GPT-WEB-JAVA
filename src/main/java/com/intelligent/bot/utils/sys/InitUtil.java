package com.intelligent.bot.utils.sys;


import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.EmailConfig;
import com.intelligent.bot.model.GptKey;
import com.intelligent.bot.model.PayConfig;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.service.sys.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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


    private static InitUtil initUtil;



    /**
     * key缓存池
     */
    private static final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();


    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        //获取启用的key
        List<GptKey> gptKeyList = gptKeyService.lambdaQuery().eq(GptKey::getState,0).orderByDesc(GptKey::getSort).list();
        gptKeyList.stream().map(GptKey::getKey).collect(Collectors.toList()).forEach(InitUtil::add);
        initUtil = this;
        initUtil.gptKeyService = this.gptKeyService;
        initUtil.asyncService = this.asyncService;
        SysConfig sysConfig = sysConfigService.getById(1);
        PayConfig payConfig = payConfigService.getById(1);
        redisUtil.setCacheObject(CommonConst.SYS_CONFIG,sysConfig);
        redisUtil.setCacheObject(CommonConst.PAY_CONFIG,payConfig);
        if(sysConfig.getRegistrationMethod() == 2){
            List<EmailConfig> emailConfigList = emailService.list();
            if(null != emailConfigList && emailConfigList.size() > 0){
                redisUtil.setCacheObject(CommonConst.EMAIL_LIST,emailConfigList);
            }
        }
    }

    /**
     * 添加key到缓存
     * @param str
     */
    public static void add(String str) {
        cache.putIfAbsent(str, str);
    }



    /**
     * 获取全部的key
     * @return
     */
    public static Collection<String> getAllKey() {
        return cache.values();
    }


    public static synchronized String getRandomKey() {
        final Collection<String> allKey = getAllKey();
        if (CollectionUtils.isEmpty(allKey)) {
            throw new E("缓存池中已无可用的Key 请联系管理员");
        }
        int index = new Random().nextInt(allKey.size());
        final List<String> list = new ArrayList<>(allKey);
        String key = list.get(index);
        initUtil.asyncService.updateKeyNumber(key);
        return key;
    }

    public static void removeKey(final String key) {
        cache.remove(key);
    }

}
