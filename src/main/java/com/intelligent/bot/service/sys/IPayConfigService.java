package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.PayConfig;
import com.intelligent.bot.model.req.sys.admin.PayConfigUpdateReq;
import com.intelligent.bot.model.res.sys.admin.PayConfigQueryRes;


public interface IPayConfigService extends IService<PayConfig> {


    B<PayConfigQueryRes> queryPayConfig();


    B<Void> update(PayConfigUpdateReq req);




}
