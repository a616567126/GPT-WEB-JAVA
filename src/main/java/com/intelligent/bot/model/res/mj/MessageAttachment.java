package com.intelligent.bot.model.res.mj;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageAttachment {

    private String url;

    @JsonProperty("proxy_url")
    private String proxyUrl;
}
