
package com.chat.java.model.gptvo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


@Accessors
@Data
public class CtlDataVo implements Serializable {

    /**
     * availableKeys
     */
    private Collection<String> availableKeys;

    /**
     * lapseKeys
     */
    private Collection<String> lapseKeys;

    /**
     * thirdPartyKey
     */
    private String thirdPartyKey;

    /**
     * use key
     */
    private String mainKey;

    /**
     * choose
     */
    private Boolean choose;
}
