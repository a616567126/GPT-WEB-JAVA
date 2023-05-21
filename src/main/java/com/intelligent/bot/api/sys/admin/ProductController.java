package com.intelligent.bot.api.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.ProductAddReq;
import com.intelligent.bot.model.req.sys.admin.ProductQueryReq;
import com.intelligent.bot.model.req.sys.admin.ProductUpdateReq;
import com.intelligent.bot.model.res.sys.admin.ProductQueryRes;
import com.intelligent.bot.service.sys.IProductService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys/product")
public class ProductController {


    @Resource
    IProductService ProductService;


    @RequestMapping(value = "/queryPage",name = "分页查询Product",method = RequestMethod.POST)
    public B<Page<ProductQueryRes>> queryPage(@Validated @RequestBody ProductQueryReq req) {
        return ProductService.queryPage(req);
    }

    @RequestMapping(value = "/add",name = "新增Product", method = RequestMethod.POST)
    public B<Void> add(@Validated @RequestBody ProductAddReq req) {
        return ProductService.add(req);
    }

    @RequestMapping(value = "/update",name = "编辑Product", method = RequestMethod.POST)
    public B<Void> update(@Validated @RequestBody ProductUpdateReq req) {
        return ProductService.update(req);
    }
    @RequestMapping(value = "/delete", name = "删除Product",method = RequestMethod.POST)
    public B<Void> delete(@Validated @RequestBody BaseDeleteEntity params) {
        return ProductService.delete(params);
    }

}
