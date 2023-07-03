package com.intelligent.bot.model.req.wx;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetTicketReq {

    @NotNull
    private Long tempUserId;
}
