
package com.chat.java.model;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class MsgSecCheckModel {


    private String content;


    private Integer version = 2;


    private Integer scene = 2;


    private String openid;
}
