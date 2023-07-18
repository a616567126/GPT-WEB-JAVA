package com.intelligent.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.GptRole;
import com.intelligent.bot.model.res.sys.admin.GptRoleQueryRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface GptRoleDao extends BaseMapper<GptRole> {


    Page<GptRoleQueryRes> queryGptRole(Page<GptRoleQueryRes> page, @Param("roleName") String roleName);


    List<GptRoleQueryRes> getGptRoleLimit10();



}
