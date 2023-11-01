package com.intelligent.bot.constant;


public class CommonConst {


    /**
     * token存储时间
     */
    public static final long TOKEN_EXPIRE_TIME = 7L;

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
     * 登录终端
     */
    public static final String USER_CLIENT = "user:client:";


    /**
     * mj回调地址
     */
    public static final String MJ_CALL_BACK_URL = "/mj/callBack";


    /**
     * 易支付回调地址
     */
    public static final String YI_PAY_CALL_BACK = "/order/yi/callback";

    /**
     * 微信支付回调地址
     */
    public static final String WX_PAY_CALL_BACK = "/order/wx/callback";


    /**
     * 易支付页面跳转地址
     */
    public static final String Yi_PAY_RETURN_RUL = "/#/mine";

    /**
     * 公众号自动回复消息类型(文本)
     */
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";

    /**
     * 公众号自动回复消息类型(图片消息)
     */
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

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
    public static final String MEDIA_ID = "N_5W6hqkdod_UDKGWseTDQDtzJiCnOCXsuVGbkvWXSEx74uuuQfUJC_1lZedJ1mL";

    /**
     * gpt3.5对话消耗次数
     */
    public static final Integer GPT_NUMBER =  2;

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
     * mj文生图极速模式
     */
    public static final Integer MJ_NUMBER_FAST =  MJ_NUMBER + 2 ;

    /**
     * mj文生图涡轮模式
     */
    public static final Integer MJ_NUMBER_TURBO =  MJ_NUMBER_FAST + 2 ;

    /**
     * mj放大消耗次数
     */
    public static final Integer MJ_U_NUMBER =  2;

    /**
     * mj变换消耗次数
     */
    public static final Integer MJ_V_NUMBER =  5;

    /**
     * mj图生文消耗次数
     */
    public static final Integer MJ_DESCRIBE_NUMBER =  3;


    /**
     * mj混合生图消耗次数
     */
    public static final Integer MJ_BLEND_NUMBER =  5;

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
     * 图生图地址
      */
    public static final String SD_IMG_2_IMG = "/sdapi/v1/img2img";


    /**
     * sd采样方法
     */
    public static final String SD_SAMPLERS = "/sdapi/v1/samplers";

    /**
     * sd画图状态
     */
    public static final String SD_PROGRESS = "/sdapi/v1/progress?skip_current_image=false";

    /**
     * sd获取图片信息接口
     */

    public static final String SD_PNG_INFO = "/sdapi/v1/png-info";

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
    public static final String DISCORD_MESSAGE_URL = "https://discord.com/api/v9/channels/%s/messages";

    /**
     * 获取任务信息时最大次数，超过该次数则将此任务改为失败
     */
    public static final Integer GET_TASK_ERROR_NUMBER = 15;

    /**
     * mj
     */
    public static final String MJ_WSS_URL = "wss://gateway.discord.gg";

    /**
     * mj 请求头
     */
    public static final String USERAGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36";


    /**
     * mj 地址
     */
    public static final String DISCORD_SERVER_URL = "https://discord.com";

    /**
     * gpt 问答地址
     */
    public static final String CPT_CHAT_URL =  "/v1/chat/completions";

    /**
     * gpt 画图地址
     */
    public static final String CPT_IMAGES_URL =  "/v1/images/generations";

    /**
     * gpt 订阅信息
     */
    public static final String CPT_SUBSCRIPTION_URL =  "/v1/dashboard/billing/subscription";
    /**
     * get 查询使用情况
     */
    public static final String CPT_BILLING_USAGE_URL =  "/v1/dashboard/billing/usage?start_date=%s&end_date=%s";

    /**
     * mj session id
     */
    public static final String MJ_SESSION_ID = "9c4055428e13bcbf2248a6b36084c5f3";

    /**
     * 默认头像地址
     */
    public static final String AVATAR = "https://guanbb.oss-accelerate.aliyuncs.com/1681116124739145728.png";

    /**
     * 线程池CorePoolSize
     */
    public static final Integer NOTIFY_POOL_SIZE = 10;


    /**
     * 微信native接口地址
     */
    public static final String WX_NATIVE_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/native";


    /**
     * google bard相关
     */
    public static final String HOSTNAME = "bard.google.com";
    public static final String BASE_URL = "https://bard.google.com/";

    public static final String ASK_QUESTION_PATH = "_/BardChatUi/data/assistant.lamda.BardFrontendService/StreamGenerate";
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36";
    public static final String TOKEN_COOKIE_1PSID = "__Secure-1PSID";
    public static final String TOKEN_COOKIE_1PSIDTS = "__Secure-1PSIDTS";
    public static final String BARD_VERSION = "boq_assistant-bard-web-server_20230808.09_p0";
    public static final String EMPTY_STRING = "";

}
