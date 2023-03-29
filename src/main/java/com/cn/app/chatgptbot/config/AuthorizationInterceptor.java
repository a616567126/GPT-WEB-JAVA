package com.cn.app.chatgptbot.config;


import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.constant.CommonConst;
import com.cn.app.chatgptbot.uitls.JwtUtil;
import com.cn.app.chatgptbot.uitls.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Slf4j
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object handler) throws IOException {
        //判断token是否存在
        log.info("请求接口地址：{}", request.getServletPath());
        long userId = JwtUtil.getUserId();
        String redisToken = RedisUtil.getCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + userId);
        String handlerToken = request.getHeader("token");
        if (StringUtils.isEmpty(redisToken) || !redisToken.equals(SecureUtil.md5(handlerToken))) {
            returnResult(httpServletResponse, 10001, "token已失效，请重新登录");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    private void returnResult(HttpServletResponse response, Integer code, String msg) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSONString(B.build(code, msg), SerializerFeature.WriteMapNullValue));
        writer.flush();
        writer.close();
    }


}
