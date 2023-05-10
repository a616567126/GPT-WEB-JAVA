package com.chat.java.mj.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TriggerSubmit {

    private String taskId;

    private String promptEn;
}
