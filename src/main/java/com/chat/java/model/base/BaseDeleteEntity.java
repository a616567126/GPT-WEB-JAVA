package com.chat.java.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author  
 * @date 2022-03-12 14:57
 */
@Data
@ApiModel(value = "BaseDeleteEntity",description = "基础删除对象")
public class BaseDeleteEntity implements Serializable {


    @NotEmpty(message = "删除数组不能为空")
    @ApiModelProperty(value = "删除id数组", position = 1)
    private List<Long> ids;
}
