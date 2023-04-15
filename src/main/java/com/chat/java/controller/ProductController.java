package com.chat.java.controller;

import com.alibaba.fastjson.JSONObject;
import com.chat.java.base.B;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.model.req.ProductAddReq;
import com.chat.java.model.req.ProductPageReq;
import com.chat.java.model.req.ProductUpdateReq;
import com.chat.java.service.IProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品表(Product)表控制层
 *
 * @author  
 * @since 2022-03-12 15:23:19
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Api(tags = {"商品管理"})
public class ProductController {


    /**
     * ProductService
     */
    final IProductService ProductService;


    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询Product")
    public B<JSONObject> queryPage(@Validated @RequestBody ProductPageReq req) {
        return ProductService.queryPage(req);
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增Product")
    public B<Void> add(@Validated @RequestBody ProductAddReq req) {
        return ProductService.add(req);
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑Product")
    public B<Void> update(@Validated @RequestBody ProductUpdateReq req) {
        return ProductService.update(req);
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除Product")
    public B<Void> delete(@Validated @RequestBody BaseDeleteEntity params) {
        return ProductService.delete(params);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ApiOperation(value = "查询商品列表/我的订单列表")
    public B<JSONObject>  list() {
        return ProductService.getList();
    }
}
