package com.intelligent.bot.service.kimi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.intelligent.bot.model.Message;
import com.intelligent.bot.service.sys.AsyncService;
import com.intelligent.bot.service.sys.IMessageLogService;
import com.intelligent.bot.utils.sys.SendMessageUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSourceListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MoonshotAiService extends EventSourceListener {

    @Resource
    AsyncService asyncService;

    @Resource
    IMessageLogService messageLogService;

    private static final String MODELS_URL = "https://api.moonshot.cn/v1/models";
    private static final String FILES_URL = "https://api.moonshot.cn/v1/files";
    private static final String ESTIMATE_TOKEN_COUNT_URL = "https://api.moonshot.cn/v1/tokenizers/estimate-token-count";
    private static final String CHAT_COMPLETION_URL = "https://api.moonshot.cn/v1/chat/completions";


    public static String getModelList(String secretKey) {
        return getCommonRequest(MODELS_URL,secretKey)
                .execute()
                .body();
    }

    public String uploadFile(@NonNull File file,String secretKey) {
        return getCommonRequest(FILES_URL,secretKey)
                .method(Method.POST)
                .header("purpose", "file-extract")
                .form("file", file)
                .execute()
                .body();
    }

    public static String getFileList(String secretKey) {
        return getCommonRequest(FILES_URL,secretKey)
                .execute()
                .body();
    }

    public static String deleteFile(@NonNull String fileId,String secretKey) {
        return getCommonRequest(FILES_URL + "/" + fileId,secretKey)
                .method(Method.DELETE)
                .execute()
                .body();
    }

    public static String getFileDetail(@NonNull String fileId,String secretKey) {
        return getCommonRequest(FILES_URL + "/" + fileId,secretKey)
                .execute()
                .body();
    }

    public  String getFileContent(@NonNull String fileId,String secretKey) {
        return getCommonRequest(FILES_URL + "/" + fileId + "/content",secretKey)
                .execute()
                .body();
    }

    public static String estimateTokenCount(@NonNull String model, @NonNull List<Message> messages,String secretKey) {
        String requestBody = new JSONObject()
                .putOpt("model", model)
                .putOpt("messages", messages)
                .toString();
        return getCommonRequest(ESTIMATE_TOKEN_COUNT_URL,secretKey)
                .method(Method.POST)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(requestBody)
                .execute()
                .body();
    }

    @SneakyThrows
    @Async
    public void chat(@NonNull String model, @NonNull List<Message> messages,String secretKey,Long logId,Long userId) {
        StringBuffer lastMessage = new StringBuffer();
        String requestBody = new JSONObject()
                .putOpt("model", model)
                .putOpt("messages", messages)
                .putOpt("stream", true)
                .toString();
        Request okhttpRequest = new Request.Builder()
                .url(CHAT_COMPLETION_URL)
                .post(RequestBody.create(MediaType.get(ContentType.JSON.getValue()), requestBody))
                .addHeader("Authorization", "Bearer " + secretKey)
                .build();
        Call call = new OkHttpClient().newCall(okhttpRequest);
        Response okhttpResponse = call.execute();
        BufferedReader reader = new BufferedReader(okhttpResponse.body().charStream());
        String line;
        while ((line = reader.readLine()) != null) {
            if (StrUtil.isBlank(line)) {
                continue;
            }
            if (JSONUtil.isTypeJSON(line)) {
                Optional.of(JSONUtil.parseObj(line))
                        .map(x -> x.getJSONObject("error"))
                        .map(x -> x.getStr("message"))
                        .ifPresent(x -> {
                            SendMessageUtil.sendMessage(userId, "Sorry!系统当前开小差了，请重试");
                            asyncService.endOfAnswer(logId,"Sorry!系统当前开小差了，请重试");
                            SendMessageUtil.sendMessage(userId, "[DONE]");
                            log.warn("kimi-error: {}",x);
                        });
                return;
            }
            line = StrUtil.replace(line, "data: ", StrUtil.EMPTY);
            if (StrUtil.equals("[DONE]", line) || !JSONUtil.isTypeJSON(line)) {
                asyncService.endOfAnswer(logId,lastMessage.toString());
                com.intelligent.bot.model.gpt.Message message = com.intelligent.bot.model.gpt.Message.ofAssistant("[DONE]");
                SendMessageUtil.sendMessage(userId, "[DONE]");
                return;
            }
            Optional.of(JSONUtil.parseObj(line))
                    .map(x -> x.getJSONArray("choices"))
                    .filter(CollUtil::isNotEmpty)
                    .map(x -> (JSONObject) x.get(0))
                    .map(x -> x.getJSONObject("delta"))
                    .map(x -> x.getStr("content"))
                    .ifPresent(x -> {
                        lastMessage.append(x);
                        log.warn("kimi回答中: {}",x);
                        SendMessageUtil.sendMessage(userId, x);
                    });
        }
    }

    private static HttpRequest getCommonRequest(@NonNull String url,String key) {
        return HttpRequest.of(url).header(Header.AUTHORIZATION, "Bearer " + key);
    }
}
