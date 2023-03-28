package com.cn.app.chatgptbot.model.res;

import lombok.Data;

import java.math.BigDecimal;

/**
 * ClassName:AdminHomeOrder
 * Package:com.cn.app.chatgptbot.model.res
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/27 - 09:09
 * @Version: v1.0
 */
@Data
public class AdminHomeOrderPrice {

    private BigDecimal price;

    private String days;
}
