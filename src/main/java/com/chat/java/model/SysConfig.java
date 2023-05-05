package com.chat.java.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.java.model.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_config")
public class SysConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 326308725675949330L;

    @ApiModelProperty(value = "注册模式 1账号密码  2 短信注册 3 关闭注册")
    private Integer registrationMethod;

    @ApiModelProperty(value = "是否禁用自动禁用key 0关闭 1开启")
    private Integer keySwitch;

    @ApiModelProperty(value = "默认注册次数")
    private Integer defaultTimes;

    @ApiModelProperty(value = "阿里云accessKeyId")
    private String aliAccessKeyId;

    @ApiModelProperty(value = "阿里云secret")
    private String aliSecret;

    @ApiModelProperty(value = "阿里云短信签名")
    private String aliSignName;

    @ApiModelProperty(value = "阿里云短信模版id")
    private String aliTemplateCode;

    @ApiModelProperty(value = "图片上传路径")
    private String imgUploadUrl;

    @ApiModelProperty(value = "图片返回前缀地址")
    private String imgReturnUrl;

    @ApiModelProperty(value = "sd接口地址")
    private String sdUrl;

    @ApiModelProperty(value = "是否开启sd 0未开启 1开启")
    private Integer isOpenSd;

    @ApiModelProperty(value = "是否开启代理 0未开启 1开启")
    private Integer isOpenProxy;

    @ApiModelProperty(value = "代理ip")
    private String proxyIp;

    @ApiModelProperty(value = "代理端口")
    private Integer proxyPort;

    @ApiModelProperty(value = "微软bing cookie")
    private String bingCookie;

    @ApiModelProperty(value = "是否开启bing 0-未开启 1开启")
    private Integer isOpenBing;


}
