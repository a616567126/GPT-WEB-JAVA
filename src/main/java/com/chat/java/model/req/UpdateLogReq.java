package com.chat.java.model.req;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * ClassName:ResetLogReq
 * Package:com.chat.java.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/24 - 22:39
 * @Version: v1.0
 */
@Data
public class UpdateLogReq {

    @NotNull(message = "日志id不能为空")
    private Long logId;

    @NotNull(message = "消息数据不能为空")
    private String newMessages;
}
