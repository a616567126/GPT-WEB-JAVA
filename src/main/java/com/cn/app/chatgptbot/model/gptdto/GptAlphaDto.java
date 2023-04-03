/**
 * @author 明明不是下雨天
 */
package com.cn.app.chatgptbot.model.gptdto;

import com.cn.app.chatgptbot.model.GptAlphaModel;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type Gpt 003 dto.
 *
 * @author bdth
 * @email 2074055628@qq.com
 */
@Data
public final class GptAlphaDto {

    /**
     * userMessages
     */
    @NotBlank(message = "消息数据不能为空")
    private String prompt;



    /**
     * OpenId (wechat)
     */
//    @NotBlank(message = "code错误")
//    private String openId;

    @NotNull(message = "登录人类型不能为空")
    private Integer type;

    private Long logId;
    /**
     * Convert to gpt alpha model gpt alpha model.
     *
     * @param item the item
     * @return the gpt alpha model
     */
    public static GptAlphaModel convertToGptAlphaModel(GptAlphaDto item) {
        if (item == null) {
            return null;
        }
        return new GptAlphaModel().setPrompt(item.getPrompt());
    }
}
