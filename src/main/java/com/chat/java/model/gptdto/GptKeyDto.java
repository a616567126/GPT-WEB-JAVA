
package com.chat.java.model.gptdto;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
public final class GptKeyDto {

    /**
     * bulkOperations Kye
     */
    @NotEmpty(message = "key不能为空")
    private List<String> keys;


}
