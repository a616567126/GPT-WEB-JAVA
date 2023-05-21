package com.intelligent.bot.utils.sys;


import cn.hutool.core.util.StrUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Objects;

@Log4j2
public class IPUtils {
    private static final String UNKNOWN = "UNKNOWN";

    private static final String LOCAL_IP = "127.0.0.1";

    public static int getClientIpInt() {
        String ipStr = getClientIpStr();
        return strToInt(ipStr);
    }

    public static int getClientIpInt(HttpServletRequest request) {
        String ipStr = getIpAddr(request);
        return strToInt(ipStr);
    }

    public static String convertIntToStr(int ipInt) {
        return String.valueOf((ipInt >> 24) & 0xff) + '.' +
                ((ipInt >> 16) & 0xff) + '.' +
                ((ipInt >> 8) & 0xff) + '.' + (ipInt & 0xff);
    }

    public static String getClientIpStr() {
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return getIpAddr(request);
    }

    /**
     * 获取客户端IP地址字符串
     *
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else {
            String[] ips = ip.split(",");
            for (int i = 0; i < ips.length; i++) {
                String strIp = ips[i];
                if (!UNKNOWN.equalsIgnoreCase(strIp) && !LOCAL_IP.equalsIgnoreCase(strIp)) {
                    ip = strIp;
                    break;
                }
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(!StrUtil.isEmpty(ip) && ip.length() > 15) {
            if(ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? LOCAL_IP : ip;
    }

    /**
     * 据位运算把 String -> int
     *
     * @param ipAddr
     * @return
     */
    private static int strToInt(String ipAddr) {
        int result = 0;
        try {
            byte[] bytes = InetAddress.getByName(ipAddr).getAddress();
            result = bytes[3] & 0xFF;
            result |= ((bytes[2] << 8) & 0xFF00);
            result |= ((bytes[1] << 16) & 0xFF0000);
            result |= ((bytes[0] << 24) & 0xFF000000);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }
}
