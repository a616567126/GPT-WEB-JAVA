package com.intelligent.bot.listener.spark;

import com.intelligent.bot.utils.spark.AuthUtils;
import lombok.Data;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.util.Objects;

/**
 * 星火大模型客户端
 *
 */
@Data
public class SparkDeskClient {

    private String host;
    private String appid;
    private String apiKey;
    private String apiSecret;
    private OkHttpClient okHttpClient;


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String host;
        private String appid;
        private String apiKey;
        private String apiSecret;
        private OkHttpClient okHttpClient;

        private Builder() {
        }

        public static Builder aSparkDeskClient() {
            return new Builder();
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder appid(String appid) {
            this.appid = appid;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public Builder okHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public SparkDeskClient build() {
            SparkDeskClient sparkDeskClient = new SparkDeskClient();
            sparkDeskClient.host = this.host;
            if (Objects.isNull(this.okHttpClient)) {
                this.okHttpClient = new OkHttpClient.Builder().build();
            }
            sparkDeskClient.okHttpClient = this.okHttpClient;
            sparkDeskClient.apiKey = this.apiKey;
            sparkDeskClient.appid = this.appid;
            sparkDeskClient.apiSecret = this.apiSecret;
            return sparkDeskClient;
        }
    }

    @SneakyThrows
    public <T extends ChatListener> WebSocket chat(T chatListener) {
        String authUrl = AuthUtils.getAuthUrl(host, apiKey, apiSecret);
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        WebSocket webSocket = this.okHttpClient.newWebSocket(request, chatListener);
        return webSocket;
    }
}
