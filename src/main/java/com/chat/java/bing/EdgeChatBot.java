package com.chat.java.bing;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chat.java.bing.model.BingChatReq;
import com.chat.java.model.SysConfig;
import com.chat.java.service.ISysConfigService;
import com.chat.java.utils.RedisUtil;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@Data
@Log4j2
public class EdgeChatBot {

    private static final String DELIMITER = "\u001E";

    private static int invocationId = 0;
    private ChatHub chatHub;

    @Resource
    RedisUtil redisUtil;
    @Resource
    ISysConfigService sysConfigService;


    @PostConstruct
    public void init() {
        try {
            SysConfig sysConfig = sysConfigService.getById(1);
            if(sysConfig.getIsOpenBing() == 1){
                create(sysConfig);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void create(SysConfig sysConfig) throws IOException {
        JSONObject conversation = createConversation(sysConfig);
        chatHub = new ChatHub(conversation);
    }

    public CompletableFuture<String> ask(BingChatReq req, Session session) {
        CompletableFuture<String> result = new CompletableFuture<>();
        chatHub.ask(req, result::complete,session);
        return result;
    }
    private static String generateRandomIP() {
        Random random = new Random();
        int a = random.nextInt(4) + 104;
        int b = random.nextInt(256);
        int c = random.nextInt(256);
        return "13." + a + "." + b + "." + c;
    }
    private static Headers generateHeaders(SysConfig sysConfig) {
        String forwardedIP = generateRandomIP();
        String uuid = UUID.randomUUID().toString();

        return new Headers.Builder()
                .add("accept", "application/json")
                .add("accept-language", "en-US,en;q=0.9")
                .add("content-type", "application/json")
                .add("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Microsoft Edge\";v=\"110\", \"Chromium\";v=\"110\"")
                .add("sec-ch-ua-arch", "\"x86\"")
                .add("sec-ch-ua-bitness", "\"64\"")
                .add("sec-ch-ua-full-version", "\"109.0.1518.78\"")
                .add("sec-ch-ua-full-version-list", "\"Chromium\";v=\"110.0.5481.192\", \"Not A(Brand\";v=\"24.0.0.0\", \"Microsoft Edge\";v=\"110.0.1587.69\"")
                .add("sec-ch-ua-mobile", "?0")
                .add("sec-ch-ua-model", "\"\"")
                .add("sec-ch-ua-platform", "\"Windows\"")
                .add("sec-ch-ua-platform-version", "\"15.0.0\"")
                .add("sec-fetch-dest", "empty")
                .add("sec-fetch-mode", "cors")
                .add("sec-fetch-site", "same-origin")
                .add("x-ms-client-request-id", uuid)
                .add("x-ms-useragent", "azsdk-js-api-client-factory/1.0.0-beta.1 core-rest-pipeline/1.10.0 OS/Win32")
                .add("Referer", "https://www.bing.com/search?q=Bing+AI&showconv=1&FORM=hpcodx")
                .add("Referrer-Policy", "origin-when-cross-origin")
                .add("x-forwarded-for", forwardedIP)
                .add("Cookie","_U="+sysConfig.getBingCookie())
                .build();
    }
    private static JSONObject createConversation(SysConfig sysConfig) throws IOException {
        Headers headers = generateHeaders(sysConfig);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.bing.com/turing/conversation/create")
                .headers(headers)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code:" + response.code());
            }
            String responseBody = response.body().string();
            return JSONObject.parseObject(responseBody);
        }
    }

    private static class ChatHub {
        private final JSONObject conversation;
        private WebSocketClient wsClient;

        public ChatHub(JSONObject conversation) {
            this.conversation = conversation;
        }

        public void ask(BingChatReq req, Consumer<String> completionHandler,Session session) {
            final String[] bot = {""};
            try {
                URI uri = new URI("wss://sydney.bing.com/sydney/ChatHub");
                Map<String, String> headersInitConver = new HashMap<>();
                headersInitConver.put("authority", "edgeservices.bing.com");
                headersInitConver.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
                headersInitConver.put("accept-language", "en-US,en;q=0.9");
                headersInitConver.put("cache-control", "max-age=0");
                headersInitConver.put("sec-ch-ua", "\"Chromium\";v=\"110\", \"Not A(Brand\";v=\"24\", \"Microsoft Edge\";v=\"110\"");
                headersInitConver.put("sec-ch-ua-arch", "\"x86\"");
                headersInitConver.put("sec-ch-ua-bitness", "\"64\"");
                headersInitConver.put("sec-ch-ua-full-version", "\"110.0.1587.69\"");
                headersInitConver.put("sec-ch-ua-full-version-list", "\"Chromium\";v=\"110.0.5481.192\", \"Not A(Brand\";v=\"24.0.0.0\", \"Microsoft Edge\";v=\"110.0.1587.69\"");
                headersInitConver.put("sec-ch-ua-mobile", "?0");
                headersInitConver.put("sec-ch-ua-model", "\"\"");
                headersInitConver.put("sec-ch-ua-platform", "\"Windows\"");
                headersInitConver.put("sec-ch-ua-platform-version", "\"15.0.0\"");
                headersInitConver.put("sec-fetch-dest", "document");
                headersInitConver.put("sec-fetch-mode", "navigate");
                headersInitConver.put("sec-fetch-site", "none");
                headersInitConver.put("sec-fetch-user", "?1");
                headersInitConver.put("upgrade-insecure-requests", "1");
                headersInitConver.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.69");
                headersInitConver.put("x-edge-shopping-flag", "1");
                headersInitConver.put("x-forwarded-for", "1.1.1.1");
                wsClient = new WebSocketClient(uri,headersInitConver) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {

                        JSONObject json = new JSONObject();
                        json.put("protocol", "json");
                        json.put("version", 1);
                        send(json.toString()+ DELIMITER);
                        JSONObject chatRequest = createChatRequest(req);
                        String message = chatRequest.toString() + DELIMITER;
                        System.out.println("===========\n"+message);
                        send(message);

                    }

                    @SneakyThrows
                    @Override
                    public void onMessage(String message) {
                        String[] messages = message.split(DELIMITER);
                        for (String msg : messages) {
                            if (msg.isEmpty()||msg.equals("{}")) {
                                continue;
                            }
                            JSONObject response = JSONObject.parseObject(msg);
                            if (response.getInteger("type") == 1){
                                JSONObject responseObject = response.getJSONArray("arguments").getJSONObject(0);
                                if(responseObject.keySet().contains("messages")){
                                    JSONArray messagesArray = responseObject.getJSONArray("messages");
                                    JSONObject adaptiveCard = messagesArray.getJSONObject(0).getJSONArray("adaptiveCards").getJSONObject(0);
                                    String responseText = adaptiveCard.getJSONArray("body").getJSONObject(0).getString("text");
                                    session.getBasicRemote().sendText("bing_"+responseText);
                                    bot[0] =responseText;
                                }
                            }else if (response.getInteger("type") == 2){
                                  completionHandler.accept(bot[0]);
                            }
                        }
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        System.out.println("WebSocket closed: " + reason);
                    }

                    @Override
                    public void onError(Exception ex) {
                        System.err.println("WebSocket error: ");
                        ex.printStackTrace();
                    }
                };
                wsClient.connectBlocking();
            } catch (URISyntaxException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        private JSONObject createChatRequest(BingChatReq req) {
            invocationId++;
            JSONObject messageObject = new JSONObject();
            messageObject.put("author", "user");
            messageObject.put("inputMethod", "Keyboard");
            messageObject.put("text", req.getPrompt());
            messageObject.put("messageType", "Chat");

            JSONObject participantObject = new JSONObject();
            participantObject.put("id", conversation.getString("clientId"));

            JSONArray optionsSets = new JSONArray();
            optionsSets.add("nlu_direct_response_filter");
            optionsSets.add("deepleo");
            optionsSets.add("disable_emoji_spoken_text");
            optionsSets.add("responsible_ai_policy_235");
            optionsSets.add("enablemm");
                  //  .put("enable_debug_commands")
            optionsSets.add(req.getMode());
            optionsSets.add("dtappid");
            optionsSets.add("trn8req120");
            optionsSets.add("h3ads");
            optionsSets.add("rai251");
            optionsSets.add("blocklistv2");
            optionsSets.add("localtime");
            optionsSets.add("dv3sugg");

            JSONObject chatRequestArguments = new JSONObject();
            chatRequestArguments.put("source", "cib");
            chatRequestArguments.put("optionsSets", optionsSets);
            chatRequestArguments.put("isStartOfSession", invocationId == 1);
            chatRequestArguments.put("message", messageObject);
            chatRequestArguments .put("conversationSignature", conversation.getString("conversationSignature"));
            chatRequestArguments .put("participant", participantObject);
            chatRequestArguments .put("conversationId", conversation.getString("conversationId"));

            JSONArray argumentsArray = new JSONArray();
            argumentsArray.add(chatRequestArguments);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("arguments", argumentsArray);
            jsonObject.put("invocationId", String.valueOf(invocationId));
            jsonObject.put("target", "chat");
            jsonObject.put("type", 4);
            return jsonObject;

        }
    }
}

