package com.intelligent.bot.service.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.dao.ProductDao;
import com.intelligent.bot.enums.sys.ResultEnum;
import com.intelligent.bot.model.Product;
import com.intelligent.bot.model.base.BaseDeleteEntity;
import com.intelligent.bot.model.req.sys.admin.ProductAddReq;
import com.intelligent.bot.model.req.sys.admin.ProductQueryReq;
import com.intelligent.bot.model.req.sys.admin.ProductUpdateReq;
import com.intelligent.bot.model.res.sys.ClientProductRes;
import com.intelligent.bot.model.res.sys.admin.ProductQueryRes;
import com.intelligent.bot.service.sys.IProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("ProductService")
@Transactional(rollbackFor = Exception.class)
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product> implements IProductService {

    @Override
    public B<Page<ProductQueryRes>> queryPage(ProductQueryReq req) {
        Page<ProductQueryRes> page = new Page<>(req.getPageNumber(),req.getPageSize());
        return B.okBuild(this.baseMapper.queryPage(page,req));
    }

    @Override
    public B<Void> add(ProductAddReq req) {
        Product product = BeanUtil.copyProperties(req, Product.class);
        Long count = this.lambdaQuery()
                .eq(null != product.getName(), Product::getName,product.getName())
                .count();
        if(count > 0){
            return B.finalBuild("商品已存在");

        }
        this.save(product);
        return B.okBuild();
    }

    @Override
    public B<Void> update(ProductUpdateReq req) {
        Product product = BeanUtil.copyProperties(req, Product.class);
        Long count = this.lambdaQuery()
                .eq(null != product.getName(), Product::getName,product.getName())
                .ne(Product::getId,product.getId())
                .count();
        if(count > 0){
            return B.build(ResultEnum.FAIL.getCode(), "商品已存在");
        }
        this.saveOrUpdate(product);
        return B.okBuild();
    }

    @Override
    public B<Void> delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        return B.okBuild();
    }

    @Override
    public List<ClientProductRes> getProductList() {
        return this.baseMapper.getProductList();
    }
}
