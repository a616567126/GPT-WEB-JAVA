/**
 * @author 明明不是下雨天
 */
package com.cn.app.chatgptbot.model.gptdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * The type Gpt bing dto.
 *
 * @author bdth
 * @email 2074055628@qq.com
 */
@Data
public final class GptCreditGrantsDto {


    @NotBlank(message = "key不能为空")
    private String key;

}
