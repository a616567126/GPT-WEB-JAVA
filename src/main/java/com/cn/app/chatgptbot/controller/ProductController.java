package com.cn.app.chatgptbot.controller;

import com.alibaba.fastjson.JSONObject;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.Product;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.service.IProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品表(Product)表控制层
 *
 * @author  
 * @since 2022-03-12 15:23:19
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Api(tags = {"商品表(Product)"})
public class ProductController {


    /**
     * ProductService
     */
    final IProductService ProductService;

    /**
     * 分页查询Product
     *
     * @param basePageHelper 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询Product")
    public B queryPage(@Validated @RequestBody BasePageHelper basePageHelper) {
        return ProductService.queryPage(basePageHelper);
    }

    /**
     * 新增Product
     *
     * @param params 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增Product")
    public B add(@Validated @RequestBody String params) {
        return ProductService.add(params);
    }

    /**
     * 编辑Product
     *
     * @param params 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "编辑Product")
    public B update(@Validated @RequestBody String params) {
        return ProductService.update(params);
    }

    /**
     * 通过id删除Product
     *
     * @param params 参数
     * @return 返回对象
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除Product")
    public B delete(@Validated @RequestBody BaseDeleteEntity params) {
        return ProductService.delete(params);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ApiOperation(value = "查询商品列表/我的订单列表")
    public B<JSONObject>  list() {
        return ProductService.getList();
    }
}
