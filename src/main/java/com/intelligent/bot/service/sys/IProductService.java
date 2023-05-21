package com.intelligent.bot.service.sys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.Product;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.ProductAddReq;
import com.intelligent.bot.model.req.sys.admin.ProductQueryReq;
import com.intelligent.bot.model.req.sys.admin.ProductUpdateReq;
import com.intelligent.bot.model.res.sys.ClientProductRes;
import com.intelligent.bot.model.res.sys.admin.ProductQueryRes;

import java.util.List;


public interface IProductService extends IService<Product> {



    B<Page<ProductQueryRes>> queryPage(ProductQueryReq req);


    B<Void> add(ProductAddReq req);


    B<Void> update(ProductUpdateReq req);


    B<Void> delete(BaseDeleteEntity params);



    List<ClientProductRes> getProductList();

}
