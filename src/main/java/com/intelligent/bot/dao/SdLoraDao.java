package com.intelligent.bot.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.SdLora;
import com.intelligent.bot.model.req.sys.admin.SdLoraQueryReq;
import com.intelligent.bot.model.res.sys.admin.SdLoraQueryRes;
import org.apache.ibatis.annotations.Param;

public interface SdLoraDao extends BaseMapper<SdLora> {

    Page<SdLoraQueryRes> queryLoraPage(Page<SdLoraQueryRes> page, @Param("req") SdLoraQueryReq req);
}
