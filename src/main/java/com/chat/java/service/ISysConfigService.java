package com.chat.java.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.java.base.B;
import com.chat.java.model.SysConfig;


public interface ISysConfigService extends IService<SysConfig> {



    B queryPage();


    B update(String params);






}
