<div align="center">
    <p style="font-size:40px;font-weight: 800;color: coral">Siana 智能Ai机器人 </p>
</div>
<div align="center" style="text-align:center;margin-top:30px;margin-bottom:20px">
    <img alt="Java version" src="https://img.shields.io/static/v1?label=openjdk&message=8&logo=openjdk" />
    <img alt="MySql version" src="https://img.shields.io/static/v1?label=mysql&message=8.0&logo=mysql&color=green" />
    <img alt="Redis version" src="https://img.shields.io/static/v1?label=redis&message=7&logo=redis&color=ff69b4" />
    <a style="padding-left:10px"><img src="https://img.shields.io/github/stars/a616567126/GPT-WEB-JAVA"/></a>
    <a style="padding-left:10px"><img src="https://img.shields.io/github/forks/a616567126/GPT-WEB-JAVA?color=red&logo=red"/></a>  


</div>

**<h1 align="center">✨  2.0全新版本，全新ui，全新体验</h1>**


## 👨‍🚀  Major Function
<h2>🔱 客户端</h2>

* **🔗登录**
* **🔗临时用户**
* **🔗注册（公众号注册，邮箱注册，账号密码注册）**
* **🔗基于SSE GPT 3.5/4.0 流式对话+上下文**
* **🔗GPT 画图**
* **🔗FlagStudio画图**
* **🔗Midjourney画图**
* **🔗Stable-Diffusion画图**
* **🔗个人信息展示（剩余次数，身份，昵称）**
* **🔗个人信息修改（头像，密码）**
* **🔗产品查询购买**
* **🔗订单查询**
* **🔗支付方式 易支付、卡密兑换**



<h2>🔱 管理端</h2>

* **🔗首页（数据统计）**
* **🔗支付配置**
* **🔗对KEY配置**
* **🔗用户管理**
* **🔗订单管理**
* **🔗公告管理**
* **🔗产品管理**
* **🔗系统配置**

## 〽️ INSTALL AND START  
    ## 系统依赖jdk1.8 其中redis mysql 8.0 需自行安装
    ## shell运行安装步骤
        1.安装mysql，redis
        2.使用centos7系统（其他系统需自己修改shell脚本），将application-prod.yml配置改为自己实际配置 复制到/usr/local/siana下
        3.进入/usr/local将脚本复制到根目录下
        4.使用sh start.sh运行安装脚本
        5.脚本将自动安装git,拉取代码，安装maven，jdk1.8，并配置环境变量
        6.自动maven打包，放到/usr/local/siana下
        7.在/etc/systemd/system/下创建bot.serice 并开机启动
        8.打包成功之后会运行systemctl restart bot 运行jar包
        9.使用journalctl -fu bot 命令可查看当前服务状态日志


## 🐋  SQL IN RESOURCES
**sql文件 src/resources/intelligent_bot.sql**  

**🧨 管理员账号admin密码123456，根据自己需求合理增加或修改表内数据，初始化sql只为正常启动代码**

## 🪄  Precautions For Using Nginx

**🕹️  若使用nginx反向代理到后端需要增加SEE支持，与SEE长连接时间**

```powershell
 server {
        listen 443 ssl http2;  # 1.1版本后这样写
        server_name baidu.com; #填写绑定证书的域名
        ssl_certificate /www/server/nginx/ssl/baidu.pem;# 指定证书的位置，绝对路径
        ssl_certificate_key /www/server/nginx/ssl/baidu.key; # 绝对路径，同上
        ssl_session_timeout 5m;
        ssl_protocols TLSv1 TLSv1.1 TLSv1.2; #按照这个协议配置
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:HIGH:!aNULL:!MD5:!RC4:!DHE;#按照这个套件配置
        ssl_prefer_server_ciphers on;
        location / {
          proxy_pass http://127.0.0.1:8080/;   #转发到tomcat
          proxy_set_header Host $http_host;  ##proxy_set_header用来重定义发往后端服务器的请求头
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto $scheme;
          proxy_buffering off;
          proxy_http_version  1.1;
          proxy_read_timeout 600s; ##设置SSE长链接保持时间为 600s
          }
    }
```

### 🧭 And coding style tests

**<h3>🧧 2.0全新ui，客户端与管理前端源码不开源，加入VIP群持续更新，加入价格500RMB</h3>**  

**[📽️后台管理ui演示地址地址](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E5%90%8E%E5%8F%B0%E7%AE%A1%E7%90%86ui%E6%BC%94%E7%A4%BA)**  

## 💪 Contributors 


<p align="center">
Our contributors have made this project possible. Thank you! 🙏
</p>

<a href="https://github.com/a616567126/GPT-WEB-JAVA/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=a616567126/GPT-WEB-JAVA" />
</a>



## 🎨USE Midjourney
- 注册 MidJourney，创建自己的频道，[参考地址](https://docs.midjourney.com/docs/quick-start)
- 添加自己的机器人 [参考地址](https://github.com/a616567126/GPT-WEB-JAVA/wiki/MJ%E6%9C%BA%E5%99%A8%E4%BA%BA%E6%B7%BB%E5%8A%A0%E8%AF%B4%E6%98%8E)  



## 🍾  Put It Last
- 易支付网站地址：[白辰易支付](https://my.mmywl.cn/)
- 作者使用服务器地址：[浅夏云](https://www.qxqxa.com/aff/ZGWPEDLQ)
- 作者使用机场地址：[新华云](https://newhua99.com/#/register?code=fMYmE5Ri)
- 默认启动时需配置如下三个表的数据（根据自己实际情况）
  - gpt_key
  - pay_config
  - sys_config
  - 项目启动时会加载对应参数到redis中，如果手动修改数据库，需要在redis中修改对应参数，防止不生效
- FlagStudio地址：http://flagstudio.baai.ac.cn/


**支付配置(pay_config)**
字段|描述|注意
-|:-:|-:
pid|易支付商户id|无
secret_key|易支付商户秘钥|无
submit_url|易支付支付请求域名|易支付发起支付的api地址，例如：https://pay888.mfysc.shop/submit.php
api_url|易支付订单查询api|后端核对订单时，易支付使用订单查询的api地址例如：https://pay888.mfysc.shop/api.php
pay_type|支付类型 0 易支付 1卡密|开启对应类型之后记得配置相关支付参数

**系统配置(sys_config)**
字段|描述|注意
-|:-:|-:
registration_method|注册模式 1 账号密码 2 邮箱注册 3 公众号 |开启邮件注册后需要在emil_config中配置邮件相关参数
default_times|默认注册次数|用户注册时默认赠送请求次数
gpt_url|gpt请求地址|可使用官方或替换第三方
img_upload_url|图片上传地址|例如：/usr/local 配置图片上传路径
img_return_url|图片域名前缀|上传图片后与图片名组合成可访问的url 例如：https://baidu.com 图片上传成功后 则返回 https://baidu.com /2023/04/26/2222.jpg
api_url|后台接口地址|用于mj、支付、微信等回调使用
client_url|客户端页面地址|用于支付跳转等
is_open_sd|是否开启sd 0未开启 1开启|无
sd_url|Sd接口地址|开启sd时需配置这个地址
sd_lora_url|sdLora地址|无
is_open_flag_studio|是否开启FlagStudio 0-未开启 1开启|无
flag_studio_key|FlagStudio key|登录之后api获得每天500次请求
flag_studio_url|FlagStudio 接口地址|暂时写死https://flagopen.baai.ac.cn/flagStudio
baidu_appid|百度appid|用于百度翻译
baidu_secret|百度Secret|用于百度翻译
baidu_key|百度应用key|用于敏感词检查
baidu_secret_key|百度应用Secret|用于敏感词检查
is_open_mj|是否开启mj 0-未开启 1开启|无
mj_guild_id|Mj服务器id|url地址中获得
mj_channel_id|Mj频道id|url地址中获得
mj_user_token|mj用户token|F12查看network中的Authorization参数
mj_bot_token|机器人token|https://discord.com/developers/applications中获取
mj_bot_name|机器人名称|默认Midjourney Bot若其他自行修改
is_open_proxy|是否开启代理 0关闭 1开启|无
proxy_ip|代理ip|无
proxy_port|代理端口|无
bing_cookie|微软bing cookie|无
is_open_bing|是否开启bing 0-未开启 1开启|无
is_open_stable_studio|是否开启StableStudio 0未开启 1 开启|无
stable_studio_api|StableStudioapi地址前缀|写死：https://api.stability.ai
stable_studio_key|StableStudio key|无





## ☕  Reward
- 支付宝  

<img src="https://user-images.githubusercontent.com/43660702/228105535-144d09cd-6326-4c22-b9b9-8c69c299caac.png" width="100px" height="100px">  

- 微信  

<img src="https://user-images.githubusercontent.com/43660702/228105188-09c49078-9156-40bc-8327-f2b05c5bc5fa.png" width="100px" height="100px"> 


## ⭐  记得点一个Star哦!!!!

## ✉  Scan code to add friends
![扫码添加好友](https://user-images.githubusercontent.com/43660702/232187172-9d971a97-b7a3-407f-9ba1-a35516505733.jpeg)



## ℹ️ Pay attention to the official account
![关注公众号](https://user-images.githubusercontent.com/43660702/229270101-4f11a841-51fc-4625-b498-833629fe7934.png)


## 
[![Star History Chart](https://api.star-history.com/svg?repos=a616567126/GPT-WEB-JAVA&type=Timeline)](https://star-history.com/#a616567126/GPT-WEB-JAVA&Timeline)  


## SPONSOR
本项目由[JetBranins](https://www.jetbrains.com/?from=Unity3DTraining)赞助相关开发工具  
<a href="https://www.jetbrains.com/?from=Unity3DTraining"><img src="https://github.com/XINCGer/Unity3DTraining/blob/master/Doc/images/jetbrains.png" width = "150" height = "150" div align=center /></a>  


## License

Apache License 2.0
