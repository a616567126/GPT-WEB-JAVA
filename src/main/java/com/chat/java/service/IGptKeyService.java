package com.chat.java.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.java.model.GptKey;
import com.chat.java.model.req.GptKeyAddReq;
import com.chat.java.model.req.UpdateKeyStateReq;
import com.chat.java.base.B;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;

/**
 * 商品表(Product)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IGptKeyService extends IService<GptKey> {



    B<JSONObject> queryPage(BasePageHelper basePageHelper);

    B<Void> add(GptKeyAddReq req);

    B<Void> updateState(UpdateKeyStateReq params);


    B<Void> delete(BaseDeleteEntity params);




}
