package com.intelligent.bot.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.bot.model.Task;
import com.intelligent.bot.model.res.sys.GalleryRes;
import com.intelligent.bot.model.res.sys.MjTaskRes;
import com.intelligent.bot.model.res.sys.MjTaskTransformRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MjTaskDao extends BaseMapper<Task> {


    List<MjTaskRes> selectUserMjTask(@Param("userId") Long userId);

    List<MjTaskTransformRes> selectTransform();

    int batchDeleteByUserId(@Param("userId") Long userId);

    int deleteByKeyId(@Param("id") Long id);

    List<GalleryRes> getGallery();
}
