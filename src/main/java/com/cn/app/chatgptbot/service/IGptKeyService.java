package com.cn.app.chatgptbot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.GptKey;
import com.cn.app.chatgptbot.model.Product;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;

/**
 * 商品表(Product)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IGptKeyService extends IService<GptKey> {


    /**
     * 分页查询Product
     *
     * @param basePageHelper 参数
     * @return 返回对象
     */
    B queryPage(BasePageHelper basePageHelper);

    /**
     * 新增Product
     *
     * @param params 参数
     * @return 返回对象
     */
    B add(String params);


    /**
     * 通过id删除Product
     *
     * @param params 参数
     * @return 返回对象
     */
    B delete(BaseDeleteEntity params);




}
