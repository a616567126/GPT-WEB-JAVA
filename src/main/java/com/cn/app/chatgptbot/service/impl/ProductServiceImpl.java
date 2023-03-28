package com.cn.app.chatgptbot.service.impl;

import cn.hutool.core.exceptions.ValidateException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.dao.OrderDao;
import com.cn.app.chatgptbot.dao.ProductDao;
import com.cn.app.chatgptbot.model.Product;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.model.res.OrderUserRes;
import com.cn.app.chatgptbot.service.IOrderService;
import com.cn.app.chatgptbot.service.IProductService;
import com.cn.app.chatgptbot.uitls.JwtUtil;
import jakarta.annotation.Resource;
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


    /**
     * 分页查询Product
     *
     * @param basePageHelper 参数
     * @return 返回对象
     */
    @Override
    public B queryPage(BasePageHelper basePageHelper) {
        JSONObject jsonObject = new JSONObject();
        Page<Product> page = new Page<>(basePageHelper.getPageNumber(),basePageHelper.getPageSize());
        QueryWrapper<Product> queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("id");
        queryWrapper.like(null != basePageHelper.getName(),"name",basePageHelper.getName());
        Page<Product> productPage = baseMapper.selectPage(page, queryWrapper);
        jsonObject.put("productPage",productPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }

    /**
     * 新增Product
     *
     * @param params 参数
     * @return 返回对象
     */
    @Override
    public B add(String params) {
        Product product = JSONObject.parseObject(params, Product.class);
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

    /**
     * 修改Product
     *
     * @param params 参数
     * @return 返回对象
     */
    @Override
    public B update(String params) {
        Product product = JSONObject.parseObject(params, Product.class);
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

    /**
     * 通过id删除Product
     *
     * @param params 参数
     * @return 返回对象
     */
    @Override
    public B delete(BaseDeleteEntity params) {
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
        return B.okBuild(jsonObject);
    }
}
