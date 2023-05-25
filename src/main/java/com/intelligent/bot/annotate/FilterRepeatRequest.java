package com.intelligent.bot.annotate;


import com.intelligent.bot.base.exception.E;
import com.intelligent.bot.config.redis.RedisLock;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.utils.sys.IPUtils;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Log4j2
public class FilterRepeatRequest {

    @Value("${spring.profiles.active}")
    private String active;

    @Resource
    private RedisLock redisLock;

    @Pointcut("@annotation(com.intelligent.bot.annotate.AvoidRepeatRequest)")
    public void arrPointcut() {
    }
    @Around("arrPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        // 获取到这个注解
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        AvoidRepeatRequest lock = method.getAnnotation(AvoidRepeatRequest.class);
        final String lockKey = generateKey(pjp);
        // 上锁
        final boolean success = redisLock.Rlock(lockKey, null,active.equals(CommonConst.ACTIVE)  ?  0 : (lock.intervalTime() == 0 ? 30 : lock.intervalTime()), TimeUnit.SECONDS);
        if (!success) {
            throw new E(lock.msg());
        }
        return pjp.proceed();
    }

    private String generateKey(ProceedingJoinPoint pjp) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        StringBuilder sb = new StringBuilder();
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        sb.append(pjp.getTarget().getClass().getName())//类名
                .append(method.getName())//方法名
                .append(IPUtils.getIpAddr(request));//ip
        for (Object o : pjp.getArgs()) {
            sb.append(o.toString());
        }
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes(Charset.defaultCharset()));
    }

}
