package com.intelligent.bot.model.base;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
public class BasePageHelper implements Serializable {


    @NotNull(message ="当前页数不能为空")
    private Integer pageNumber;

    @NotNull(message ="每页条数不能为空")
    private Integer pageSize;

}
