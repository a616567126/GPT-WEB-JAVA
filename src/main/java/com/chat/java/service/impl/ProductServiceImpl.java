package com.chat.java.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.java.model.PayConfig;
import com.chat.java.model.Product;
import com.chat.java.model.req.ProductAddReq;
import com.chat.java.model.req.ProductPageReq;
import com.chat.java.model.req.ProductUpdateReq;
import com.chat.java.model.res.OrderUserRes;
import com.chat.java.utils.JwtUtil;
import com.chat.java.base.B;
import com.chat.java.base.ResultEnum;
import com.chat.java.dao.OrderDao;
import com.chat.java.dao.ProductDao;
import com.chat.java.model.base.BaseDeleteEntity;
import com.chat.java.model.base.BasePageHelper;
import com.chat.java.service.IProductService;

import javax.annotation.Resource;

import com.chat.java.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户表(Product)表服务实现类
 *
 * @author  
 * @since 2022-03-12 15:23:55
 */
@Service("ProductService")
@Transactional(rollbackFor = Exception.class)
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product> implements IProductService {

    @Resource
    OrderDao orderDao;



    @Override
    public B<JSONObject> queryPage(ProductPageReq req) {
        JSONObject jsonObject = new JSONObject();
        Page<Product> page = new Page<>(req.getPageNumber(),req.getPageSize());
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.like(null != req.getName(),"name",req.getName());
        Page<Product> productPage = baseMapper.selectPage(page, queryWrapper);
        jsonObject.put("productPage",productPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }


    @Override
    public B<Void> add(ProductAddReq req) {
        Product product = BeanUtil.copyProperties(req, Product.class);
        if(product.getType() == 1){
            Long count = this.lambdaQuery()
                    .eq(Product::getType,1)
                    .count();
            if(count > 0){
                return B.finalBuild("月卡只可以存在一种");

            }
        }
        Long count = this.lambdaQuery()
                .eq(null != product.getName(), Product::getName,product.getName())
                .eq(null != product.getType(), Product::getType,product.getType())
                .count();
        if(count > 0){
            return B.finalBuild("商品已存在");

        }
        this.save(product);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }


    @Override
    public B<Void> update(ProductUpdateReq req) {
        Product product = BeanUtil.copyProperties(req, Product.class);
        if (null == product || null == product.getId()) {
            return B.finalBuild("参数校验失败");
        }
        Product oldProduct = this.getById(product.getId());
        if(oldProduct.getType() == 1 &&
                (oldProduct.getMonthlyNumber() != product.getMonthlyNumber()
                        || product.getType() != oldProduct.getType())){
            return B.finalBuild("月卡无法进行编辑");
        }
        Long count = this.lambdaQuery()
                .eq(null != product.getName(), Product::getName,product.getName())
                .eq(null != product.getType(), Product::getType,product.getType())
                .ne(Product::getId,product.getId())
                .count();
        if(count > 0){
            return B.build(ResultEnum.FAIL.getCode(), "商品已存在");
        }
        this.saveOrUpdate(product);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }


    @Override
    public B<Void> delete(BaseDeleteEntity params) {
        this.removeByIds(params.getIds());
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    @Override
    public B<JSONObject> getList() {
        List<Product> list = this.lambdaQuery().orderByAsc(Product::getType).orderByDesc(Product::getSort).list();
        List<OrderUserRes> userOrderList = orderDao.getUserOrderList(JwtUtil.getUserId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productList",list);
        jsonObject.put("orderList",userOrderList);
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
        jsonObject.put("payType",payConfig.getPayType());
        return B.okBuild(jsonObject);
    }
}
