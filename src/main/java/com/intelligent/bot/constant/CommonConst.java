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
     * mj-task
     */
    public static final String KEY_PREFIX = "mj-task::";

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
    public static final String MJ_CALL_BACK_URL = "/mj/callBack";


    /**
     * 易支付回调地址
     */
    public static final String YI_PAY_CALL_BACK = "/order/yi/callback";


    /**
     * 易支付页面跳转地址
     */
    public static final String Yi_PAY_RETURN_RUL = "/#/product";

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
     * gpt3.5对话消耗次数
     */
    public static final Integer GPT_NUMBER =  1;

    /**
     * gpt4消耗次数
     */
    public static final Integer GPT_4_NUMBER =  5;

    /**
     * gpt画图消耗次数
     */
    public static final Integer GPT_OFFICIAL_NUMBER =  2;


    /**
     * fs画图消耗次数
     */
    public static final Integer FS_NUMBER =  2;


    /**
     * mj画图消耗次数
     */
    public static final Integer MJ_NUMBER =  5;

    /**
     * sd画图消耗次数
     */
    public static final Integer SD_NUMBER =  2;

    /**
     * bing消耗次数
     */
    public static final Integer BING_NUMBER =  3;


    /**
     * StableStudio画图消耗次数
     */
    public static final Integer STABLE_STUDIO_NUMBER =  3;

    /**
     * gpt 3.5 token 最大限制
     */
    public static final Integer GPT_3_5_TURBO_0301_TOKENS = 4096;

    /**
     * dev环境
     */
    public static final String ACTIVE = "dev";


    /**
     * stableStudio用户信息地址
     */
    public static final String STABLE_STUDIO_USER_ACCOUNT = "/v1/user/account";

    /**
     * stableStudio账户余额地址
     */
    public static final String STABLE_STUDIO_USER_BALANCE = "/v1/user/balance";

    /**
     * 获取引擎列表
     */
    public static final String STABLE_STUDIO_ENGINES_LIST = "/v1/engines/list";

    /**
     * 文生图
      */
    public static final String STABLE_STUDIO_TEXT_TO_IMAGE = "/v1/generation/%s/text-to-image";

    /**
     * emoji表情
     */
    public static final String EMOJI = "[\\ud83c\\udf00-\\ud83d\\udfff|\\ud83e\\udd00-\\ud83e\\uddff|\\ud83d\\udc00-\\ud83d\\ude4f|\\ud83d\\ude80-\\ud83d\\udeff]";

    /**
     * 画图队列数量
     */
    public static final Integer QUEUE_SIZE = 5;

    /**
     * 画图队列存储key
     */
    public static final String QUEUE_KEY = "user:queue";

    /**
     * sd文生图
     */
    public static final String SD_TXT_2_IMG = "/sdapi/v1/txt2img";

    /**
     * sd采样方法
     */
    public static final String SD_SAMPLERS = "/sdapi/v1/samplers";

    /**
     * sd画图状态
     */
    public static final String SD_PROGRESS = "/sdapi/v1/progress?skip_current_image=false";

    /**
     * mj队列并发数
     */
    public static final Integer MJ_TASK_QUEUE_CORE_SIZE = 3;

    /**
     * mj等待队列长度
     */
    public static final Integer MJ_TASK_QUEUE_QUEUE_SIZE = 3;

    /**
     * 任务超时时间(分钟)
     */
    public static final Integer TIMEOUT_MINUTES = 5;

    /**
     * mj请求地址
     */
    public static final String DISCORD_API_URL = "https://discord.com/api/v9/interactions";

    /**
     * mj图片上传地址
     */
    public static final String DISCORD_UPLOAD_URL = "https://discord.com/api/v9/channels/%s/attachments";

    /**
     * mj消息地址
     */
    public static final String DISCORD_MESSAGE_URL = "https://discord.com/api/v10/channels/%s/messages?limit=10";

}
