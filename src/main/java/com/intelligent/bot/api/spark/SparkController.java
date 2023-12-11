package com.intelligent.bot.api.spark;

import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.req.spark.ChatReq;
import com.intelligent.bot.service.spark.ISparkService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/spark")
@Log4j2
public class SparkController {

    @Resource
    ISparkService sparkService;

    @PostMapping(value = "/chat",name = "星火大模型提交")
    public B<Long> chat(@RequestBody ChatReq req) {
        return B.okBuild(sparkService.chat(req));
    }
}
