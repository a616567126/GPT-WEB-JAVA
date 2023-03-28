package com.cn.app.chatgptbot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.Order;
import com.cn.app.chatgptbot.model.RefuelingKit;
import com.cn.app.chatgptbot.model.req.CreateOrderReq;
import com.cn.app.chatgptbot.model.req.ReturnUrlReq;
import com.cn.app.chatgptbot.model.res.CreateOrderRes;

/**
 * 用户表(User)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IRefuelingKitService extends IService<RefuelingKit> {

    Long getUserKitId();




}
