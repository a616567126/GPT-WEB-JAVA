package com.intelligent.bot.utils.sys;

import cn.hutool.crypto.SecureUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Slf4j
public class JwtUtil {

    private static final String ISSUSER = "SYSTEM";

    private static final String SUBJECT = "BEAN";

    private static final String AUDIENCE = "SSP";

    private static final String TOKEN_KEY = "RdecmaE4As3HhQ51vz6OZgtkMW7FfCb0";


    /**
     * 创建Token
     * @return
     */
    public static String createToken(User user) {
        return getTokenString(user);
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
    public static Long getUserId() {
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

    public static Integer getType() {
        JWTVerifier verifier = getJWTVerifier();
        String token = getRequestToken();
        if(Strings.isBlank(token)) {
            return 0;
        }
        try {
            /** 校验Token有效性 **/
            DecodedJWT decoded = verifier.verify(token);
            /** 用户类型 **/
            return decoded.getClaim("type").asInt();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return 0;
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
                .withClaim("type",user.getType())
                .sign(algorithm);
        RedisUtil.setCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + user.getId(), SecureUtil.md5(token),
                CommonConst.TOKEN_EXPIRE_TIME, TimeUnit.DAYS);
        RedisUtil.setCacheObject(CommonConst.USER_CLIENT + user.getId(), user.getIsMobile(),
                CommonConst.TOKEN_EXPIRE_TIME, TimeUnit.DAYS);
        return token;
    }




    private static JWTVerifier getJWTVerifier() {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY);
        return JWT.require(algorithm)
                .withIssuer(ISSUSER)
                .withAudience(AUDIENCE)
                .build();
    }

    private static String getRequestToken() {
        if(null == RequestContextHolder.getRequestAttributes()){
            return null;
        }
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if(Strings.isBlank(token)) {
            return null;
        }
        return token;
    }
}
