/**
 * @author 明明不是下雨天
 */
package com.chat.java.model;

import lombok.Data;
import lombok.experimental.Accessors;



@Data
@Accessors(chain = true)
public class GptAlphaModel {


    private String model = "image-alpha-001";


    private String prompt;


    private String size = "512x512";


    private Integer num_images = 1;


    private String response_format = "url";


}
