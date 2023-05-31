package com.intelligent.bot.api.sys.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.base.BasePageHelper;
import com.intelligent.bot.model.res.sys.admin.ErrorMessageQueryRes;
import com.intelligent.bot.service.sys.IErrorMessageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/sys/error/message")
public class ErrorMessageController {


    @Resource
    IErrorMessageService errorMessageService;

    @RequestMapping(value = "/queryPage",name = "查询异常日志", method = RequestMethod.POST)
    public B<Page<ErrorMessageQueryRes>> queryPage(@Validated @RequestBody BasePageHelper req) {
        return errorMessageService.queryPage(req);
    }

}
