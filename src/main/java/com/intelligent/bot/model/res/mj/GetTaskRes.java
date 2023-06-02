package com.intelligent.bot.model.res.mj;

import com.intelligent.bot.enums.mj.TaskStatus;
import lombok.Data;

@Data
public class GetTaskRes {

    private String taskId;

    private TaskStatus status;

    private String url;

    private String proxyUrl;

    private String percentage;

    private String baseUrl;


}
