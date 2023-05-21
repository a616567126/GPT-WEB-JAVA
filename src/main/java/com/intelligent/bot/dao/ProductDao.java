package com.intelligent.bot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.model.Product;
import com.intelligent.bot.model.req.sys.admin.ProductQueryReq;
import com.intelligent.bot.model.res.sys.ClientProductRes;
import com.intelligent.bot.model.res.sys.admin.ProductQueryRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ProductDao extends BaseMapper<Product> {


    Page<ProductQueryRes> queryPage(@Param("page") Page<ProductQueryRes> page, @Param("req") ProductQueryReq req);

    List<ClientProductRes> getProductList();


}
