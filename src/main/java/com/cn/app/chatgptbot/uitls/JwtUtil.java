package com.cn.app.chatgptbot.uitls;

import cn.hutool.crypto.SecureUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cn.app.chatgptbot.constant.CommonConst;
import com.cn.app.chatgptbot.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 基于Jwt的Token基础操作工具类
 * @author  
 * @date 2022-03-25 11:00
 */
@Slf4j
public class JwtUtil {

    private static String ISSUSER = "SYSTEM";

    private static String SUBJECT = "BEAN";

    private static String AUDIENCE = "SSP";

    private static String TOKEN_KEY = "RdecmaE4As3HhQ51vz6OZgtkMW7FfCb0";


    /**
     * 创建Token
     * @return
     */
    public static String createToken(User user) {
        try {
            return getTokenString(user);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 校验Token有效性
     * @return
     */
    public static boolean verifierToken() {
        String token = getRequestToken();
        if(Strings.isBlank(token)) {
            return false;
        }
        JWTVerifier verifier = getJWTVerifier();
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            log.error(exception.getMessage());
            return false;
        }
    }

    /**
     * 获取Token剩余有效时间（毫秒）
     * @return
     */
    public static long getRemainingTime() {
        String token = getRequestToken();
        if(Strings.isBlank(token)) {
            return 0L;
        }
        JWTVerifier verifier = getJWTVerifier();
        try {
            /** 校验Token有效性 **/
            DecodedJWT decoded = verifier.verify(token);
            /** 获取当前时间 **/
            Date date = new Date();
            /** 获取过期时间 **/
            Date expireTime = decoded.getExpiresAt();
            long remainingTime = expireTime.getTime() - date.getTime();
            if(remainingTime < 0) {
                return 0L;
            } else {
                return remainingTime;
            }
        } catch (JWTVerificationException exception) {
            log.error(exception.getMessage());
            return 0L;
        }
    }



    /**
     * 根据Token获取用户编号
     * @return
     */
    public static long getUserId() {
        JWTVerifier verifier = getJWTVerifier();
        String token = getRequestToken();
        if(Strings.isBlank(token)) {
            return 0L;
        }
        try {
            /** 校验Token有效性 **/
            DecodedJWT decoded = verifier.verify(token);
            /** 获取用户编号 **/
            return decoded.getClaim("userId").asLong();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return 0L;
        }
    }



    private static String getTokenString(User user) {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY);
        Date date = new Date();
        String token = JWT.create()
                .withIssuer(ISSUSER)
                .withSubject(SUBJECT)
                .withAudience(AUDIENCE)
                .withIssuedAt(date)
                .withExpiresAt(new Date(date.getTime() + 3600L * 1000L * 8L))
                .withClaim("userId",user.getId())
                .withClaim("name",user.getName())
                .sign(algorithm);
        RedisUtil.setCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + user.getId(), SecureUtil.md5(token),
                CommonConst.TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return token;
    }




    private static JWTVerifier getJWTVerifier() {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUSER)
                .withAudience(AUDIENCE)
                .build();
        return verifier;
    }

    private static String getRequestToken() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if(Strings.isBlank(token)) {
            return null;
        }
        return token;
    }
}
