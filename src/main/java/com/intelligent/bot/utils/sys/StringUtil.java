package com.intelligent.bot.utils.sys;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;

public class StringUtil {
    
    public static String toUnderlineCase(Object req){
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return JSONObject.toJSONString(req, config);
    }
}
