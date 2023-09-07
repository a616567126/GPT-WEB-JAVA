package com.intelligent.bot.server.wss;

import com.intelligent.bot.constant.CommonConst;
import com.intelligent.bot.model.SysConfig;
import com.intelligent.bot.utils.sys.RedisUtil;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocketFactory;

public interface WebSocketStarter {

	void setTrying(boolean trying);

	void start() throws Exception;

	default WebSocketFactory createWebSocketFactory() {
		SysConfig cacheObject = RedisUtil.getCacheObject(CommonConst.SYS_CONFIG);
		WebSocketFactory webSocketFactory = new WebSocketFactory().setConnectionTimeout(10000);
		if (cacheObject.getIsOpenProxy() == 1) {
			ProxySettings proxySettings = webSocketFactory.getProxySettings();
			proxySettings.setHost(cacheObject.getProxyIp());
			proxySettings.setPort(cacheObject.getProxyPort());
		}
		return webSocketFactory;
	}
}
