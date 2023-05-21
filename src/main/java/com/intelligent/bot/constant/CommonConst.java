package com.intelligent.bot.constant;


public class CommonConst {


    /**
     * token存储时间
     */
    public static final long TOKEN_EXPIRE_TIME = 1000 * 3600 * 24 * 7L;

    /**
     * token 前缀
     */
    public static final String REDIS_KEY_PREFIX_TOKEN = "TOKEN:";


    /**
     * 邮箱验证码前缀
     */
    public static final String SEND_EMAIL_CODE = "email:code";

    /**
     * 系统配置redis前缀
     */
    public static final String SYS_CONFIG = "sysConfig";

    /**
     * 支付配置redis前缀
     */
    public static final String PAY_CONFIG = "payConfig";

    /**
     * email redis前缀
     */
    public static final String EMAIL_LIST = "emailList";

    /**
     * mail 发送标题
     */
    public static final String EMAIL_TITLE = "Siana";

    /**
     * 百度token
     */
    public static final String BAIDU_TO_EXAMINE_REDIS_KEY = "baidu:token";

    /**
     * fstoken
     */
    public static final String FLAG_STUDIO_TOKEN = "flag:studio";


    /**
     * mj回调地址
     */
    public static final String MJ_CALL_BACK_URL = "/trigger/callBack";


    /**
     * 易支付回调地址
     */
    public static final String YI_PAY_CALL_BACK = "/order/yi/callback";


    /**
     * 易支付页面跳转地址
     */
    public static final String Yi_PAY_RETURN_RUL = "/#/user/product";

    /**
     * 公众号自动回复消息类型
     */
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";

    /**
     * 事件推送
     */
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";

    /**
     * 大写字母
     */
    public static final String CAPITAL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 小写字母
     */
    public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";


    /**
     * 数字
     */
    public static final String DIGIT = "1234567890";

    /**
     * 永久素材id
     */
    public static final String MEDIA_ID = "yQW1hbBnX-2LcEXW8kghesqMWfIM2S4IfA6SXevO-bUiafR51ufOUCRPtyEshUhy";

    /**
     * gpt消耗次数
     */
    public static final Integer GPT_NUMBER =  1;

    /**
     * gpt画图消耗次数
     */
    public static final Integer GPT_OFFICIAL_NUMBER =  2;


    /**
     * fs画图消耗次数
     */
    public static final Integer FS_NUMBER =  2;


    /**
     * mh画图消耗次数
     */
    public static final Integer MJ_NUMBER =  3;

    /**
     * sd画图消耗次数
     */
    public static final Integer SD_NUMBER =  2;

    /**
     * bing消耗次数
     */
    public static final Integer BING_NUMBER =  3;

    /**
     * gpt 3.5 token 最大限制
     */
    public static final Integer GPT_3_5_TURBO_0301_TOKENS = 4096;

    /**
     * dev环境
     */
    public static final String ACTIVE = "dev";
}
