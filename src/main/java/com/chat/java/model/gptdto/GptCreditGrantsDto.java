/**
 * @author 明明不是下雨天
 */
package com.chat.java.model.gptdto;

import javax.validation.constraints.NotBlank;
import lombok.Data;



@Data
public final class GptCreditGrantsDto {


    @NotBlank(message = "key不能为空")
    private String key;

}
