package com.chat.java.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.java.model.RefuelingKit;
import com.chat.java.model.res.UserRefuelingKitRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName:RefuelingKitDao
 * Package:com.chat.java.dao
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/22 - 09:50
 * @Version: v1.0
 */
public interface RefuelingKitDao extends BaseMapper<RefuelingKit> {

    List<UserRefuelingKitRes> selectUserKit(@Param("userId") Long userId);

    Long getUserKitId(@Param("userId") Long userId);
}
