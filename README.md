<div align="center">
    <p style="font-size:40px;font-weight: 800;color: coral">Siana 智能Ai机器人 </p>
</div>
<div align="center" style="text-align:center;margin-top:30px;margin-bottom:20px">
    <img alt="Java version" src="https://img.shields.io/static/v1?label=openjdk&message=8&logo=openjdk" /> &nbsp; &nbsp;
    <img alt="MySql version" src="https://img.shields.io/static/v1?label=mysql&message=8.0&logo=mysql&color=green" />&nbsp; &nbsp;
    <img alt="Redis version" src="https://img.shields.io/static/v1?label=redis&message=7&logo=redis&color=ff69b4" />&nbsp; &nbsp;
    <a style="padding-left:10px"><img src="https://img.shields.io/github/stars/a616567126/GPT-WEB-JAVA"/></a>&nbsp; &nbsp;
    <a style="padding-left:10px"><img src="https://img.shields.io/github/forks/a616567126/GPT-WEB-JAVA?color=red&logo=red"/></a>&nbsp; &nbsp;


</div>

<h1 align="center"> 💣演示地址已关闭</h1>
<p align="center">🧧 3.0全新ui，客户端与管理端移动端，购买后加入VIP群持续更新，扫码下方作者微信添加好友咨询加群</p>
<p align="center"> 后台管理ui演示地址地址：https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E5%90%8E%E5%8F%B0%E7%AE%A1%E7%90%86ui%E6%BC%94%E7%A4%BA </p>

## 🍾  作者推荐
- 作者使用服务器地址：[浅夏云香港美国国内服务器8核心16G内存低至690/年详情扫码联系作者](https://www.qxqxa.com/aff/ZGWPEDLQ)
- 作者使用机场地址：[新华云](https://newhua99.com/#/register?code=fMYmE5Ri)
- 9.9刀洛杉矶小鸡不限流量1c1g：[rabisu](https://www.rabisu.com/panel/link.php?id=10)


<h2>📌 功能项</h2>

- [x] GPt对话(基于SSE WebSocket流式推送,支持3.5,4.0等模型,支持官方,第三方api地址,支持GPT4.0图片识别,dall-e-3模型)
- [x] 星火大模型(基于SSE WebSocket流式推送,支持2,3,3.5模型)
- [x] Midjourney画图(/imagine(文生图),/describe(图生文),重做,--relax,--fast(切换出图模式),U放大，V变换,Strong,Subtle,U2x,U4x,ZoomOut 2x,ZoomOut 1.5x,➡️⬅️⬆️⬇️位置偏移,/shorten(咒语解析),/blend(混合生图),垫图,账号池)
- [x] Stable-Diffusion画图(模型选择,Lora选择,支持高清修复,垫图)
- [x] 个人信息(剩余次数，身份，昵称,头像，密码)
- [x] 产品查询购买
- [x] 支付方式 易支付、卡密兑换、微信支付
- [x] 图片存储方式本地/oss存储，可动态配置


## 💬  使用 GPT
- 1.在gpt_key中配置对应的gpt key，注意区分3.5与4.0
- 2.若国内环境使用请使用代理访问，或使用cloudflare搭理，[教程地址](https://github.com/x-dr/chatgptProxyAPI)
- 3.gpt使用sse方式进行消息推送与前端交互，若使用nginx请查看上方nginx配置


## 🧩  使用 Image Upload(图片上传)
- 1.创建指定的文件夹如：/usr/local/upload
- 2.创建成功后在"sys_config"表中"img_upload_url"配置第一步创建的目录记得最后边加上"/"如：/usr/local/upload/
- 3.使用nginx进行文件夹代理
- 4.nginx代理的域名或ip配置到sys_config中img_return_url如："https://www.baidu.com"
- 5.上传的图片会以每天的年月日来进行创建文件夹
- 6.图片名称分为两种，Midjourney的名字为任务id，其余的图片为当前时间戳
- 7.图片最终的地址为："img_return_url"+"img_upload_url"+文件名，如："https://www.baidu.com/20230618/123.jpg"


## 🎨  使用 Stable-Diffusion
- 1.在"sd_model"表中配置模型（名字（全部内容包括后缀），图片）
- 2.若有lora在"sd_lora"表中配置lora（名字，图片）
- 3.配置"sys_config"表中"is_open_sd"为1，开启状态
- 4.配置"sys_config"表中"sd_url"的地址，本地默认地址为http://127.0.0.1:7860(记得打开api开关)


## 🎨  使用 Midjourney
- 1.注册 MidJourney创建自己的频道、[参考地址](https://docs.midjourney.com/docs/quick-start)
- 2.添加成功之后查看浏览器中的地址如：<SMALL>https://discord.com/channels/123/456 </SMALL> 其中123为mj_guild_id,456为mj_channel_id
- 3.获取mj_user_token，浏览器打开F12随便发送一个信息查看Network,Authorization为用户token


## 🪜  使用 Proxy（代理）
<p align="center">GPT、Midjourney 国内网络环境下使用代理访问</p>  

- 代理使用，配置流程、[参考地址](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E4%BD%BF%E7%94%A8%E4%BB%A3%E7%90%86%E8%AF%B7%E6%B1%82GPT%E3%80%81Midjourney)


## 📄  使用 百度翻译，内容审核  
<p align="center">GPT、Midjourney、Stable-Diffusion 使用文本审核，Midjourney、Stable-Diffusion，使用百度翻译</p>    

- 1.百度翻译申请，配置流程、[参考地址](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E7%94%B3%E8%AF%B7%E7%99%BE%E5%BA%A6%E7%BF%BB%E8%AF%91)
- 1.百度文本审核申请，配置流程、[参考地址](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E7%94%B3%E8%AF%B7%E7%99%BE%E5%BA%A6%E5%86%85%E5%AE%B9%E5%AE%A1%E6%A0%B8%E5%B9%B3%E5%8F%B0-%E6%96%87%E6%9C%AC)


## 🥤  Reward（有能力的可以请作者喝一杯冰可落）
- 支付宝  

<img src="https://user-images.githubusercontent.com/43660702/228105535-144d09cd-6326-4c22-b9b9-8c69c299caac.png" width="100px" height="100px">  

- 微信  

<img src="https://user-images.githubusercontent.com/43660702/228105188-09c49078-9156-40bc-8327-f2b05c5bc5fa.png" width="100px" height="100px"> 

## ✉  Scan code to add friends（扫码添加微信好友）⭐  记得点一个Star哦!!!!
![扫码添加好友](https://user-images.githubusercontent.com/43660702/232187172-9d971a97-b7a3-407f-9ba1-a35516505733.jpeg)



## 
[![Star History Chart](https://api.star-history.com/svg?repos=a616567126/GPT-WEB-JAVA&type=Timeline)](https://star-history.com/#a616567126/GPT-WEB-JAVA&Timeline)  


[![Powered by DartNode](https://dartnode.com/branding/DN-Open-Source-sm.png)](https://dartnode.com "Powered by DartNode - Free VPS for Open Source")
## License

Apache License 2.0
