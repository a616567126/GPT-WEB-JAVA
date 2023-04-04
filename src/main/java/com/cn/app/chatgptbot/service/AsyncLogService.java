package com.cn.app.chatgptbot.service;

import com.cn.app.chatgptbot.model.GptKey;
import com.cn.app.chatgptbot.model.UseLog;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.service.IGptKeyService;
import com.cn.app.chatgptbot.service.IUseLogService;
import com.cn.app.chatgptbot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName:AsynLogService
 * Package:com.cn.app.chatgptbot.api
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
    public void saveKeyLog(GptKey gptKey,User user){
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
