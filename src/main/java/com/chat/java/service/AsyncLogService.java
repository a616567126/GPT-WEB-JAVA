package com.chat.java.service;

import com.chat.java.model.GptKey;
import com.chat.java.model.UseLog;
import com.chat.java.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName:AsynLogService
 * Package:com.chat.java.api
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/23 - 14:55
 * @Version: v1.0
 */
@Component
public class AsyncLogService {


    @Autowired
    private IUserService userService;

    @Autowired
    private IUseLogService useLogService;

    @Autowired
    private IGptKeyService gptKeyService;



    @Async
    public void saveKeyLog(GptKey gptKey, User user){
       this.gptKeyService.saveOrUpdate(gptKey);
        this.userService.saveOrUpdate(user);
    }

    @Async
    public void updateKeyNumber(String key){
        GptKey gptKey =  gptKeyService.lambdaQuery().eq(GptKey::getKey, key).one();
        gptKey.setUseNumber(gptKey.getUseNumber()+1);
        gptKeyService.saveOrUpdate(gptKey);
    }

    @Async
    public void updateKeyNumber(String key,Integer number){
        GptKey gptKey =  gptKeyService.lambdaQuery().eq(GptKey::getKey, key).one();
        gptKey.setUseNumber(gptKey.getUseNumber()+number);
        gptKeyService.saveOrUpdate(gptKey);
    }

    @Async
    public void updateKeyState(List<String> list){
       gptKeyService.lambdaUpdate().in(GptKey::getKey,list).set(GptKey::getState,1).update();
    }

    @Async
    public void saveUseLog(UseLog useLog){
        this.useLogService.save(useLog);
    }
}
