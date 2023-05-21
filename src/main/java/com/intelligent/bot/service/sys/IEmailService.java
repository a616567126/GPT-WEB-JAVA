package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.EmailConfig;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.EmailConfigAddReq;
import com.intelligent.bot.model.req.sys.admin.EmailConfigQueryReq;
import com.intelligent.bot.model.req.sys.admin.EmailConfigUpdateReq;
import com.intelligent.bot.model.res.sys.admin.EmailConfigQueryRes;


public interface IEmailService extends IService<EmailConfig> {


    B<Page<EmailConfigQueryRes>> queryPage(EmailConfigQueryReq req);


    B<Void> add(EmailConfigAddReq req);


    B<Void> update(EmailConfigUpdateReq req);


    B<Void> delete(BaseDeleteEntity req);




}
