package com.chat.java.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.java.model.PayConfig;
import com.chat.java.base.B;
import com.chat.java.model.req.PayConfigUpdateReq;

/**
 * 商品表(Product)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IPayConfigService extends IService<PayConfig> {



    B<JSONObject> queryPage();


    B<Void> update(PayConfigUpdateReq req);






}
