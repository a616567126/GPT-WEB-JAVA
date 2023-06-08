package com.intelligent.bot.base.exception;

import com.intelligent.bot.base.result.B;
import com.intelligent.bot.model.ErrorMessage;
import com.intelligent.bot.service.sys.IErrorMessageService;
import com.intelligent.bot.utils.sys.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Log4j2
public class ExceptionHandle {

    @Resource
    IErrorMessageService errorMessageService;

    /**
     * 异常处理
     * @param e 异常信息
     * @return 返回类是我自定义的接口返回类，参数是返回码和返回结果，异常的返回结果为空字符串
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public B<Void> handle(Exception e) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorMessage(e.getMessage());
        errorMessage.setUrl(request.getServletPath());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            if(stackTraceElement.toString().contains("com.intelligent.bot")){
                errorMessage.setPosition(stackTraceElement.toString());
            }
        }
        errorMessage.setErrorMessage(null == errorMessage.getErrorMessage() ? e.getStackTrace()[0].toString() : errorMessage.getErrorMessage());
        errorMessage.setUserId(JwtUtil.getUserId());
        errorMessageService.save(errorMessage);
        //自定义异常返回对应编码
        if (e instanceof E) {
            E ex = (E) e;
            return B.finalBuild(ex.getMessage());
        }
        //其他异常报对应的信息
        else {
            log.error("[系统异常]{}", e.getMessage());
            return B.finalBuild("系统异常");
        }

    }
}
