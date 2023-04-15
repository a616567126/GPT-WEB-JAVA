package com.chat.java.controller.gpt;

import com.chat.java.base.B;
import com.chat.java.utils.GptUtil;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Api(tags = {"当前系统中gptkey状态查询"})
public final class GptKeyApi {


    @GetMapping(value = "/chat/status", name = "RunningStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public B getAppStatus() {
        return B.buildGptData(GptUtil.getCtlDataVo());
    }




}
