
package com.chat.java.utils;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.chat.java.model.billing.BillingUsage;
import com.chat.java.constant.CommonConst;
import com.chat.java.exception.CustomException;
import com.chat.java.service.IGptKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeoutException;


@Slf4j
@RequiredArgsConstructor
@Component
public final class WebClientUtil {


    @Autowired
    IGptKeyService gptKeyService;


    public static JSONObject build(ClientHttpConnector connector, final String url, final Object body, final String openKey,Long useLogId) {

        final String block = WebClient.builder()
                .clientConnector(connector)
                .baseUrl(CommonConst.OPEN_AI_URL)
                .defaultHeader("Authorization", "Bearer " + openKey)
                .build()
                .post()
                .uri(url)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            JSONObject errorObj = JSONObject.parseObject(errorBody);
                            final String errorCode = errorObj.getString("error");
                            final JSONObject jsonObject = JSONObject.parseObject(errorCode);
                            final String type = jsonObject.getString("type");
                            final String code = jsonObject.getString("code");
                            if ("access_terminated".equals(type)) {
                                InitUtil.removeKey(Collections.singletonList(openKey));
                                InitUtil.getRandomKey(openKey,useLogId);
                                return Mono.error(new CustomException("目前分配的OpenKey已遭封禁(系统已重新分配KEY),请尝试重新发送消息_"+useLogId));
                            } else if ("invalid_request_error".equals(type)) {
                                if ("invalid_api_key".equals(code)) {
                                    InitUtil.removeKey(Collections.singletonList(openKey));
                                    InitUtil.getRandomKey(openKey,useLogId);
                                    return Mono.error(new CustomException("目前分配的OpenKey已经失效(系统已重新分配KEY),请尝试请重新发送消息_"+useLogId));
                                } else {
                                    InitUtil.removeKey(Collections.singletonList(openKey));
                                    InitUtil.getRandomKey(openKey,useLogId);
                                    return Mono.error(new CustomException("请求已被OpenAi拒绝受理(系统已重新分配KEY),请尝试重新发送消息_"+useLogId));
                                }
                            } else {
                                return Mono.error(new CustomException("请求过于频繁,请稍后再试_"+useLogId));
                            }

                        }))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(120))
                .onErrorMap(TimeoutException.class, e -> new CustomException("回复时间过长,建议点击垃圾箱清理会话数据_"+useLogId))
                .onErrorMap(Exception.class, e -> new CustomException("请求过于频繁,请稍后再试_"+useLogId))
                .block();
        JSONObject jsonObject = JSONObject.parseObject(block);
        jsonObject.put("logId",useLogId);
        return jsonObject;
    }
    public static BillingUsage build(final String url, final String openKey) {
        final BillingUsage billingUsage = WebClient.builder()
                .baseUrl(CommonConst.OPEN_AI_URL)
                .defaultHeader("Authorization", "Bearer " + openKey)
                .build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(BillingUsage.class)
                .timeout(Duration.ofSeconds(120))
                .onErrorMap(TimeoutException.class, e -> new CustomException("查询余额失败(超时)"))
                .onErrorMap(Exception.class, e -> new CustomException("查询余额失败"))
                .block();
        assert billingUsage != null;
        billingUsage.getDailyCosts().forEach(d ->{
            d.setDate(cn.hutool.core.date.DateUtil.format(DateUtil.date(d.getTimestamp()*1000L), "yyyy-MM-dd"));
        });
        return billingUsage;
    }

}
