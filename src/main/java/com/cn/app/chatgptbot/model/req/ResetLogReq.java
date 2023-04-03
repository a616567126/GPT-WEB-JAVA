package com.cn.app.chatgptbot.model.req;

import com.cn.app.chatgptbot.model.GptTurboModel;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * ClassName:ResetLogReq
 * Package:com.cn.app.chatgptbot.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/24 - 22:39
 * @Version: v1.0
 */
@Data
public class ResetLogReq {

    @NotNull(message = "日志id不能为空")
    private Long logId;

    @NotNull(message = "消息数据不能为空")
    private String newMessages;

}
