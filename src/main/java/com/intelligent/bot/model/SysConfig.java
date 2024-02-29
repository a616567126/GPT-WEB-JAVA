package com.intelligent.bot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.intelligent.bot.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_config")
public class SysConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    /**
     * 注册模式  1 账号密码 2 邮箱注册 3 公众号
     */
    private Integer registrationMethod;

    /**
     * 默认注册次数
     */
    private Integer defaultTimes;

    /**
     * gpt请求地址
     */
    private String gptUrl;

    /**
     * gpt4请求地址
     */
    private String gpt4Url;


    /**
     * gpt4内容识别请求地址
     */
    private String gpt4VisionUrl;


    /**
     * gpt开关 0-未开启、1-开启gpt3.5、2-开启gpt4.0、3-全部
     */
    private Integer isOpenGpt;

    /**
     * gpt画图开关 0-未开启、1-开启
     */
    private Integer isOpenGptOfficial;

    /**
     * gpt内容识别开关 0-未开启、1开启
     */
    private Integer isOpenGptVision;

    /**
     * 图片上传路径
     */
    private String imgUploadUrl;

    /**
     * 图片返回前缀地址
     */
    private String imgReturnUrl;

    /**
     * 后台接口地址
     */
    private String apiUrl;

    /**
     * 客户端页面地址
     */
    private String clientUrl;

    /**
     * sd接口地址
     */
    private String sdUrl;

    /**
     * 是否开启sd 0未开启 1开启
     */
    private Integer isOpenSd;

    /**
     * 是否开启FlagStudio 0-未开启 1开启
     */
    private Integer isOpenFlagStudio;

    /**
     * FlagStudio key
     */
    private String flagStudioKey;

    /**
     * FlagStudio接口地址
     */
    private String flagStudioUrl;

    /**
     * 百度appid
     */
    private String baiduAppid;

    /**
     * 百度Secret
     */
    private String baiduSecret;
    /**
     * 百度应用key
     */
    private String baiduKey;

    /**
     * 百度应用Secretbranch0delbr
     */
    private String baiduSecretKey;

    /**
     * 是否开启mj 0未开启 1开启
     */
    private Integer isOpenMj;

    /**
     * 是否开启代理 0未开启 1开启
     */
    private Integer isOpenProxy;

    /**
     * 代理ip
     */
    private String proxyIp;

    /**
     * 代理端口
     */
    private Integer proxyPort;

    /**
     * 微软bing cookie
     */
    private String bingCookie;

    /**
     * 是否开启bing 0-未开启 1开启
     */
    private Integer isOpenBing;

    /**
     * 是否开启StableStudio 0未开启 1 开启
     */
    private Integer isOpenStableStudio;

    /**
     * StableStudioApi地址前缀
     */
    private String stableStudioApi;

    /**
     * StableStudio key
     */
    private String stableStudioKey;

    /**
     *  客户端 logo 地址
     */
    private String clientLogo;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 谷歌bardToken
     */
    private String bardToken;

    /**
     * AI会话默认角色
     */
    private String defaultRole;

    /**
     * 星火模型开关 0 -未开启 、1-开启
     */
    private Integer isOpenSpark;

    /**
     * 星火APPID
     */
    private String sparkAppId;

    /**
     * 星火APIKey
     */
    private String sparkApiKey;

    /**
     * 星火APISecret
     */
    private String sparkApiSecret;
}
