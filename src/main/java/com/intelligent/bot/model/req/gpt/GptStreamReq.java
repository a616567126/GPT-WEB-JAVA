package com.intelligent.bot.model.req.gpt;


import lombok.Data;

@Data
public class GptStreamReq {

    private String problem;

    private Long logId;

    private Integer type = 3;

    private String role;

    /**
     * 准确性
     */
    private double temperature = 0.2;

    /**
     * 随机性
     */
    private double topP = 1d;

}
