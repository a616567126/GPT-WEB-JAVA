package com.cn.app.chatgptbot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cn.app.chatgptbot.model.GptKey;
import com.cn.app.chatgptbot.model.UseLog;
import org.apache.ibatis.annotations.Param;

/**
 * 商品表(Product)表数据库访问层
 *
 * @author  
 * @since 2022-03-12 14:35:54
 */
public interface UseLogDao extends BaseMapper<UseLog> {


    Integer getDayUseNumber(@Param("userId") Long userId);



}
