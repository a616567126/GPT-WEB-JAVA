package com.intelligent.bot.model.req.sys;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@NoArgsConstructor(force = true)
public class CreatePinOrderReq {


    /**
     * 卡密
     */
    @NonNull
    private String cardPin;

}
