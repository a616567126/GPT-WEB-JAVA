/**
 * @author 明明不是下雨天
 */
package com.chat.java.utils;

import com.chat.java.model.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;


@Slf4j
@Component
public class ProxyUtil {




    /**
     * Gets proxy.
     *
     * @return the proxy
     */
    public ReactorClientHttpConnector getProxy() {
        SysConfig sysConfig = RedisUtil.getCacheObject("sysConfig");
        HttpClient httpClient;
        if(sysConfig.getIsOpenProxy() == 1){
            httpClient = HttpClient.create().tcpConfiguration((tcpClient) -> tcpClient.proxy(proxy -> proxy
                    .type(ProxyProvider.Proxy.HTTP)
                    .host(sysConfig.getProxyIp())
                    .port(sysConfig.getProxyPort())));
        }else {
            httpClient = HttpClient.create().tcpConfiguration((tcpClient) -> {
                return tcpClient;
            });
        }
        return new ReactorClientHttpConnector(httpClient);
    }
}
