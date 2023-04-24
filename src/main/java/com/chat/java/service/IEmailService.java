package com.chat.java.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.java.base.B;
import com.chat.java.model.EmailConfig;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.EmailConfigAddReq;
import com.chat.java.model.req.EmailConfigPageReq;
import com.chat.java.model.req.EmailConfigUpdateReq;


public interface IEmailService extends IService<EmailConfig> {


    B<JSONObject> queryPage(EmailConfigPageReq req);


    B<Void> add(EmailConfigAddReq req);


    B<Void> update(EmailConfigUpdateReq req);


    B<Void> delete(BaseDeleteEntity params);


}
