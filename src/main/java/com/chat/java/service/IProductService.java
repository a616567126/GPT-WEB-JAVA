package com.chat.java.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.java.model.Product;
import com.chat.java.base.B;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.ProductAddReq;
import com.chat.java.model.req.ProductPageReq;
import com.chat.java.model.req.ProductUpdateReq;


public interface IProductService extends IService<Product> {


    B<JSONObject> queryPage(ProductPageReq req);


    B<Void> add(ProductAddReq req);


    B<Void> update(ProductUpdateReq req);


    B<Void> delete(BaseDeleteEntity params);

    B<JSONObject>  getList();




}
