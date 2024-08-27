package com.intelligent.bot.api.kimi;

import com.alibaba.fastjson.JSONObject;
import com.intelligent.bot.annotate.AvoidRepeatRequest;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.req.kimi.KimiStreamReq;
import com.intelligent.bot.service.kimi.MoonshotAiService;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.IMessageLogService;
import com.intelligent.bot.utils.sys.FileUtil;
import com.intelligent.bot.utils.sys.InitUtil;
import com.intelligent.bot.utils.sys.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/kimi")
@Log4j2
public class KimiController {


    @Resource
    IMessageLogService messageLogService;

    @Resource
    MoonshotAiService moonshotAiService;

    @Resource
    AsyncService asyncService;

    @PostMapping(value = "/kimi/chat", name = "文件上传")
    @AvoidRepeatRequest()
    public B<JSONObject> kimiChat(@Validated @RequestBody KimiStreamReq req) {
        String secretKey = InitUtil.getRandomKey(2);
        Long logId = messageLogService.createMessageId(req.getLogId(), req.getFileUrl(), null, 2,req.getFileName());
        File file = FileUtil.createFile(req.getFileUrl());
        String result = moonshotAiService.uploadFile(file, secretKey);
        String fileContent = moonshotAiService.getFileContent(JSONObject.parseObject(result).getString("id"), secretKey);
        List<com.intelligent.bot.model.Message> messageList = new ArrayList<>();
        com.intelligent.bot.model.Message message = new com.intelligent.bot.model.Message();
        message.setRole("system");
        message.setContent(fileContent);
        messageList.add(message);
        message = new com.intelligent.bot.model.Message();
        message.setRole("user");
        message.setContent("请帮我描述这个文件的内容，【重要】1.请不要用markdown语法输出 2.请仔细阅读之后把你描述的内容分析给我！");
        messageList.add(message);
        moonshotAiService.chat("moonshot-v1-8k", messageList, secretKey, logId, JwtUtil.getUserId());
        asyncService.deleteFile(file);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("logId", logId);
        return B.okBuild(jsonObject);
    }


}
