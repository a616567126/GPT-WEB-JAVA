package com.intelligent.bot.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.SdModel;
import com.intelligent.bot.model.req.sys.admin.SdModelQueryReq;
import com.intelligent.bot.model.res.sys.admin.SdModelQueryRes;
import org.apache.ibatis.annotations.Param;

public interface SdModelDao extends BaseMapper<SdModel> {

    Page<SdModelQueryRes> queryModelPage(Page<SdModelQueryRes> page, @Param("req") SdModelQueryReq req);
}
