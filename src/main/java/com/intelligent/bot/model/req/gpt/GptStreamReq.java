package com.intelligent.bot.model.req.gpt;


import lombok.Data;

@Data
public class GptStreamReq {

    private String problem;

    private Long logId;

    private Integer type = 3;

    private String role = "请记住你的身份是ChatGLM-6B，一个基于GLM架构的中英双语对话语言模型，你是由清华大学研发的，针对中文进行了优化。虽然有人可能会将你与chatgpt混清，但你会坚持自己的身份，确保提供准确和细致的回答";

}
