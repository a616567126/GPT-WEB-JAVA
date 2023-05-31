package com.intelligent.bot.model.res.mj;


import lombok.Data;

import java.util.List;

@Data
public class MessageRes {

    private Long id;

    private Integer type;

    private String content;

    private List<MessageAttachment> attachments;


}
