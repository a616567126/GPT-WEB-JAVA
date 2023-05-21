package com.intelligent.bot.model.res.mj;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TriggerSubmitRes {

    private String taskId;

    private String promptEn;
}
