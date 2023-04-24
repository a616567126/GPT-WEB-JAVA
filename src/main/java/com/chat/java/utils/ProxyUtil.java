/**
 * @author 明明不是下雨天
 */
package com.chat.java.utils;

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
     * broker
     */
    @Value(value = "${broker.ip}")
    private String ip;

    /**
     * port
     */
    @Value(value = "${broker.port}")
    private Integer port;



    /**
     * Gets proxy.
     *
     * @return the proxy
     */
    public ReactorClientHttpConnector getProxy() {
        HttpClient httpClient = HttpClient.create().tcpConfiguration((tcpClient) -> tcpClient.proxy(proxy -> proxy
            .type(ProxyProvider.Proxy.HTTP)
            .host("1081")
            .port(1081)));
        return new ReactorClientHttpConnector(httpClient);
    }
}
