package com.chat.java.config;

import cn.hutool.crypto.digest.DigestUtil;
import com.chat.java.exception.CustomException;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author shenshipeng
 * @date 2022-11-11 09:11
 */
@Configuration
@Aspect
@Log4j2
public class FilterRepeatRequest {

    @Resource
    RedissonClient redissonClient;

    // 定义 注解 类型的切点
    @Pointcut("@annotation(com.chat.java.config.AvoidRepeatRequest)")
    public void arrPointcut() {
    }

    // 实现过滤重复请求功能
    @Around("arrPointcut()")
    public Object arrBusiness(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes sra = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        // 获取方法的 AvoidRepeatRequest 注解
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        AvoidRepeatRequest arr = method.getAnnotation(AvoidRepeatRequest.class);
        // 判断是否是重复的请求
        String tokenMd5 = DigestUtil.md5Hex(getIp(request) + request.getRequestURI());
        RLock lock = redissonClient.getLock(tokenMd5);
        boolean res = lock.tryLock(3L, arr.intervalTime(), TimeUnit.SECONDS);
        if (res) {
            return joinPoint.proceed();
        } else {
            throw new CustomException(arr.msg());
        }
    }
    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null) {
            if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                } else {
                    return ip;
                }
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null) {
            if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
