package com.chat.java.model.billing;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName:OpenAiResponse
 * Package:com.chat.java.model
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/23 - 16:57
 * @Version: v1.0
 */
@Data
public class OpenAiResponse<T> implements Serializable {
    private String object;
    private List<T> data;
    private Error error;


    @Data
    public class Error {
        private String message;
        private String type;
        private String param;
        private String code;
    }
}
