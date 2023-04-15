package com.chat.java.constant;

/**
 * @author  
 * @date 2020-08-10 14:17
 */
public class CommonConst {


    //token存储时间
    public static final long TOKEN_EXPIRE_TIME = 1000 * 3600 * 24 * 7L;

    //win系统
    public static final String WINDOWS = "Windows";

    //win系统存放位置
    public static final String WINDOWSPATH = "E:\\upload";

    //linux存放位置
    public static final String LINUXPATH = "/usr/local/images";

    /**
     * token 前缀
     */
    public static final String REDIS_KEY_PREFIX_TOKEN = "TOKEN:";

    /**
     * openid接口地址
     */
    public static final String OPEN_AI_URL = "https://api.openai.com/";

}
