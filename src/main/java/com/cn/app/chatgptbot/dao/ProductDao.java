package com.cn.app.chatgptbot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cn.app.chatgptbot.model.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品表(Product)表数据库访问层
 *
 * @author  
 * @since 2022-03-12 14:35:54
 */
public interface ProductDao extends BaseMapper<Product> {



}
