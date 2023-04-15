/**
 * @author 明明不是下雨天
 */
package com.chat.java.model.gptdto;

import javax.validation.constraints.NotBlank;

import lombok.Data;



@Data
public final class GptBingDto {

    /**
     * message
     */
    @NotBlank(message = "请求参数不能为空")
    private String param;


    /**
     * whether it is streaming data
     */
    private boolean isStream;

    /**
     * openId (weChat)
     */
    @NotBlank(message = "code错误")
    private String openId;
}
