package com.chat.java.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ClassName:WxService
 * Package:com.chat.java.service
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/31 - 16:41
 * @Version: v1.0
 */
public interface WxService {

    String callbackEvent(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
