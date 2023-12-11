package com.intelligent.bot.model.spark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutHeader {
    /**
     * 错误码，0表示正常，非0表示出错；详细释义可在接口说明文档最后的错误码说明了解
     * https://www.xfyun.cn/doc/spark/%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E.html
     */
    private int code;
    /**
     * 会话状态，取值为[0,1,2]；0代表首次结果；1代表中间结果；2代表最后一个结果
     */
    private int status;
    /**
     * 会话是否成功的描述信息
     */
    private String message;
    /**
     * 会话的唯一id，用于讯飞技术人员查询服务端会话日志使用,出现调用错误时建议留存该字段
     */
    private String sid;

    /**
     * 错误码，0表示正常，非0表示出错；详细释义可在接口说明文档最后的错误码说明了解<br>
     * https://www.xfyun.cn/doc/spark/%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E.html
     */
    @Getter
    public enum Code {
        SUCCESS(0),
        ;

        Code(int value) {
            this.value = value;
        }

        private final int value;
    }
}
