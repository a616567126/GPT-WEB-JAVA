
package com.chat.java.utils;

import com.chat.java.model.PayConfig;
import com.chat.java.model.SysConfig;
import com.chat.java.service.AsyncLogService;
import com.chat.java.exception.CustomException;
import com.chat.java.model.GptKey;
import com.chat.java.service.IGptKeyService;
import com.chat.java.model.gptvo.CtlDataVo;
import com.chat.java.service.IPayConfigService;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.chat.java.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
@Log4j2
public final class InitUtil {

    @Autowired
    IGptKeyService gptKeyService;
    @Resource
    AsyncLogService asyncLogService;
    @Resource
    IPayConfigService payConfigService;
    @Resource
    ISysConfigService sysConfigService;

    @Resource
    RedisUtil redisUtil;

    private static InitUtil initUtil;


    /**
     * effective
     */
    private static final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    /**
     * lapse
     */
    private static final ConcurrentHashMap<String, String> lapse = new ConcurrentHashMap<>();

    /**
     * choose
     */
    public static boolean choose;

    /**
     * use key
     */
    public static String mainKey = "";




    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        // traverse the insert
        List<GptKey> gptKeyList = gptKeyService.lambdaQuery().eq(GptKey::getState, 0).orderByDesc(GptKey::getSort).list();
        gptKeyList.stream().map(GptKey::getKey).collect(Collectors.toList()).forEach(InitUtil::add);
        final Collection<String> allKey = getAllKey();
        final List<String> list = allKey.stream().collect(Collectors.toList());
        // get the first one
        mainKey = list.get(0);
        initUtil = this;
        initUtil.gptKeyService = this.gptKeyService;
        initUtil.asyncLogService = this.asyncLogService;
        PayConfig payConfig = payConfigService.getById(1);
        SysConfig sysConfig = sysConfigService.getById(1);
        redisUtil.setCacheObject("payConfig", payConfig);
        redisUtil.setCacheObject("sysConfig", sysConfig);
    }

    /**
     * Gets main key.
     *
     * @return the main key
     */
    public static String getMainKey() {
        return mainKey;
    }


    /**
     * Add.
     *
     * @param str the str
     */
    public static void add(String str) {
        cache.putIfAbsent(str, str);
    }


    /**
     * Gets random key.
     */
    public static synchronized void getRandomKey(final String openKey, Long useLogId) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Collection<String> allKey = getAllKey();
        if (CollectionUtils.isEmpty(allKey)) {
            throw new CustomException("缓存池中已无可用的Key 请联系管理员_" + useLogId);
        }
        int index = new Random().nextInt(allKey.size());
        final List<String> list = allKey.stream().collect(Collectors.toList());
        final String str = list.get(index);
        if (getMainKey().equals(openKey)) {
            mainKey = cache.get(str);
            lapse.put(openKey, openKey);
            initUtil.asyncLogService.updateKeyNumber(openKey);
        }
    }

    public static synchronized Integer getRandomKey(final String openKey) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Collection<String> allKey = getAllKey();
        if (CollectionUtils.isEmpty(allKey)) {
            return -1;
        }
        int index = new Random().nextInt(allKey.size());
        final List<String> list = allKey.stream().collect(Collectors.toList());
        final String str = list.get(index);
        if (getMainKey().equals(openKey)) {
            mainKey = cache.get(str);
            lapse.put(openKey, openKey);
            initUtil.asyncLogService.updateKeyNumber(openKey);
        }
        return 0;
    }

    public static synchronized String wxGetRandomKey(final String openKey) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Collection<String> allKey = getAllKey();
        if (CollectionUtils.isEmpty(allKey)) {
            return "缓存池中已无可用的Key 请联系管理员";
        }
        int index = new Random().nextInt(allKey.size());
        final List<String> list = allKey.stream().collect(Collectors.toList());
        final String str = list.get(index);
        if (getMainKey().equals(openKey)) {
            mainKey = cache.get(str);
            lapse.put(openKey, openKey);
            initUtil.asyncLogService.updateKeyNumber(openKey);
        }
        return null;
    }

    /**
     * Remove key.
     *
     * @param list the list
     */
    public static void removeKey(final List<String> list) {
        list.forEach(cache::remove);
        SysConfig config = RedisUtil.getCacheObject("sysConfig");
        if (config.getKeySwitch() == 1) {
            initUtil.asyncLogService.updateKeyState(list);
        }
    }

    /**
     * Get all key collection.
     *
     * @return the collection
     */
    public static Collection<String> getAllKey() {
        return cache.values();
    }


    public static CtlDataVo getCtlDataVo() {
        final CtlDataVo ctlDataVo = new CtlDataVo();
        ctlDataVo.setChoose(choose);
        ctlDataVo.setAvailableKeys(getAllKey());
        ctlDataVo.setLapseKeys(getLapseKey());
        ctlDataVo.setThirdPartyKey(null);
        ctlDataVo.setMainKey(mainKey);
        return ctlDataVo;
    }


    /**
     * Gets lapse key.d
     *
     * @return the lapse key
     */
    public static Collection<String> getLapseKey() {
        return lapse.values();
    }
}
