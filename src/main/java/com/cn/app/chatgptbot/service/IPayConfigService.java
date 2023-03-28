package com.cn.app.chatgptbot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.GptKey;
import com.cn.app.chatgptbot.model.PayConfig;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;

/**
 * 商品表(Product)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IPayConfigService extends IService<PayConfig> {



    B queryPage();


    B update(String params);






}
