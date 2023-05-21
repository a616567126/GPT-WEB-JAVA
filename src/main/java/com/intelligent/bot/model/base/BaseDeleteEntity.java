package com.intelligent.bot.model.base;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;


@Data
public class BaseDeleteEntity implements Serializable {


    @NotEmpty(message = "删除数组不能为空")
    private List<Long> ids;
}
