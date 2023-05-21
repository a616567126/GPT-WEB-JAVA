package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.GptKey;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.req.sys.admin.GptKeyAddReq;
import com.intelligent.bot.model.req.sys.admin.GptKeyUpdateStateReq;
import com.intelligent.bot.model.res.sys.admin.GptKeyQueryRes;

public interface IGptKeyService extends IService<GptKey> {


    B<Page<GptKeyQueryRes>> queryPage(BasePageHelper basePageHelper);

    B<Void> add(GptKeyAddReq req);

    B<Void> delete(BaseDeleteEntity req);

    B<Void> updateState(GptKeyUpdateStateReq req);




}
