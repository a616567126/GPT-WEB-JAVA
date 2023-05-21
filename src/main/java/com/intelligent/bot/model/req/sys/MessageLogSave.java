package com.intelligent.bot.model.req.sys;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MessageLogSave {


    private String type;

    private String prompt;

    private List<String> imgList;

    private String startTime;

    private String endTime;
}
