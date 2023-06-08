package com.intelligent.bot.model.req.gpt;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GptBillingReq {

    @NotBlank(message = "key不能为空")
    private String key;

}
