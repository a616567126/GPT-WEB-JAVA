package com.chat.java.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.java.model.RefuelingKit;

/**
 * 用户表(User)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IRefuelingKitService extends IService<RefuelingKit> {

    Long getUserKitId(Long userId);




}
