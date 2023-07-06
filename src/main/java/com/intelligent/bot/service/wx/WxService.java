package com.intelligent.bot.service.wx;

import me.chanjar.weixin.common.error.WxErrorException;

import javax.servlet.http.HttpServletRequest;


public interface WxService {

    String callbackEvent(HttpServletRequest request) throws Exception;

    void mj( Long id, Integer index,  Integer action,String fromUserName) throws WxErrorException;
}
