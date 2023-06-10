package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.SdLora;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.SdLoraAddReq;
import com.intelligent.bot.model.req.sys.admin.SdLoraQueryReq;
import com.intelligent.bot.model.req.sys.admin.SdLoraUpdateReq;
import com.intelligent.bot.model.res.sys.admin.SdLoraQueryRes;


public interface ISdLoraService extends IService<SdLora> {

    B<Page<SdLoraQueryRes>> queryLoraPage(SdLoraQueryReq req);

    B<Void> addLora(SdLoraAddReq req);

    B<Void> updateLora(SdLoraUpdateReq req);

    B<Void> deleteLora(BaseDeleteEntity req);

}
