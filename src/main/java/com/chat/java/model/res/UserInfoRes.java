package com.chat.java.model.res;

import com.chat.java.model.UseLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName:CreateOrderReq
 * Package:com.chat.java.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/22 - 09:03
 * @Version: v1.0
 */
@Data
public class UserInfoRes implements Serializable {


    @ApiModelProperty(value = "用户名")
    private String name;
    @ApiModelProperty(value = "0 普通用户 1 月卡用户 2 管理员")
    private Integer type;

    @ApiModelProperty(value = "月卡到期日期", position = 11)
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "剩余次数", position = 12)
    private Integer remainingTimes;

    @ApiModelProperty(value = "月卡今日剩余次数", position = 12)
    private Integer dayRemainingTimes;

    @ApiModelProperty(value = "加油包信息", position = 12)
    private List<UserRefuelingKitRes> kitList;

    @ApiModelProperty(value = "首页排序最高公告内容", position = 12)
    private String content;

    @ApiModelProperty(value = "对话列表最新10条", position = 12)
    private List<UseLog> logList;

}
