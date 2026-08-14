<div align="center">
    <p style="font-size:40px;font-weight: 800;color: coral; margin-bottom: 10px;">Siana 智能 AI 机器人</p>
    <img alt="Java version" src="https://img.shields.io/static/v1?label=openjdk&message=8&logo=openjdk" /> &nbsp;
    <img alt="MySql version" src="https://img.shields.io/static/v1?label=mysql&message=8.0&logo=mysql&color=green" /> &nbsp;
    <img alt="Redis version" src="https://img.shields.io/static/v1?label=redis&message=7&logo=redis&color=ff69b4" /> &nbsp;
    <img src="https://img.shields.io/github/stars/a616567126/GPT-WEB-JAVA" alt="GitHub stars"/> &nbsp;
    <img src="https://img.shields.io/github/forks/a616567126/GPT-WEB-JAVA?color=red&logo=red" alt="GitHub forks"/>
</div>

---

<div align="center">
    <h1 style="color: #ff4d4f;">💣 演示地址已关闭</h1>
    <p style="font-size: 16px; font-weight: bold;">🧧 3.0 全新 UI，客户端与管理端移动端适配</p>
    <p>购买后加入 <b>VIP 群</b> 持续更新，扫码下方作者微信添加好友咨询加群</p>
    <p>🔗 <a href="https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E5%90%8E%E5%8F%B0%E7%AE%A1%E7%90%86ui%E6%BC%94%E7%A4%BA" target="_blank">后台管理 UI 演示地址</a></p>
</div>

---

## 🔥 <span style="font-size: 26px; color: #ff4d4f;"><b>作者推荐（独家福利）</b></span>

* **🚀 独立服务器推荐**：**[浅夏云香港美国国内服务器 | 8核心16G内存低至 690/年 | 详情扫码联系作者](https://www.qxqxa.com/aff/ZGWPEDLQ)**
* **✈️ 优质高速机场**：**[新华云点此注册](https://newhua99.com/#/register?code=fMYmE5Ri)**
* **🛸 极致性价比小鸡**：**[Rabisu | 9.9刀洛杉矶小鸡不限流量 1C1G](https://www.rabisu.com/panel/link.php?id=10)**

---

## 📌 功能项

- [x] **GPT 对话**：基于 SSE WebSocket 流式推送，支持 3.5、4.0 等全模型系列，支持官方/第三方 API 地址，支持 GPT4.0 图片识别、DALL-E 3 画图模型。
- [x] **星火大模型**：基于 SSE WebSocket 流式推送，支持 2.0、3.0、3.5 模型。
- [x] **Midjourney 画图**：支持 `/imagine` (文生图)、`/describe` (图生文)、重做、`--relax`、`--fast` (切换出图模式)、U 放大、V 变换、Strong、Subtle、U2x、U4x、ZoomOut 2x、ZoomOut 1.5x、位置偏移偏转、`/shorten` (咒语解析)、`/blend` (混合生图)、垫图、账号池管理。
- [x] **Stable-Diffusion 画图**：支持模型选择、Lora 选择、高清修复、垫图。
- [x] **用户中心**：管理个人信息（剩余次数、身份标签、昵称、头像、密码）。
- [x] **商业闭环**：产品查询与购买，集成**易支付**、**卡密兑换**、**微信支付**。
- [x] **图片存储**：支持本地存储 / 阿里云 OSS 存储，支持在后台动态切换配置。

---

## 💬 使用 GPT
1. 在 `gpt_key` 中配置对应的 GPT Key，注意区分 3.5 与 4.0。
2. 若国内环境使用请配置代理访问，或使用 Cloudflare 搭建，[参考教程地址](https://github.com/x-dr/chatgptProxyAPI)。
3. GPT 使用 SSE 方式进行消息推送与前端交互，若使用 Nginx 部署请务必关闭 Buffer 缓存（具体配置见上文）。

## 🧩 使用 Image Upload（图片上传）
1. 在服务器创建指定的文件夹，例如：`/usr/local/upload`。
2. 创建成功后，在 `sys_config` 表的 `img_upload_url` 字段中配置该目录（**注意：末尾记得加上斜杠 `/`**），如：`/usr/local/upload/`。
3. 使用 Nginx 对该文件夹进行静态资源代理。
4. 将 Nginx 代理的域名或 IP 配置到 `sys_config` 表中的 `img_return_url` 字段中，如：`https://www.yourdomain.com`。
5. 上传的图片会自动以当天日期（年月日）创建子文件夹。
6. 图片命名规范：Midjourney 图片名称固定为**任务 ID**，其余图片名称为**当前时间戳**。
7. 图片最终访问绝对路径为：`img_return_url` + `img_upload_url` + 文件名。

## 🎨 使用 Stable-Diffusion
1. 在 `sd_model` 表中配置模型信息（包含完整后缀的模型名字、预览图片）。
2. 若需要使用 Lora，在 `sd_lora` 表中进行配置（Lora 名字、预览图片）。
3. 将 `sys_config` 表中的 `is_open_sd` 字段配置为 `1`（开启状态）。
4. 配置 `sys_config` 表中的 `sd_url` 地址，本地默认通常为 `http://127.0.0.1:7860`（**注意：启动时请务必打开 `--api` 开关**）。

## 🎨 使用 Midjourney
1. 注册 Midjourney 并创建属于自己的私密服务器/频道，[参考官方快速开始文档](https://docs.midjourney.com/docs/quick-start)。
2. 邀请机器人进入频道，查看浏览器中的连接地址，如：`https://discord.com/channels/123/456`，其中 `123` 即为 `mj_guild_id`，`456` 即为 `mj_channel_id`。
3. 获取 `mj_user_token`：登录 Discord 网页端并打开 F12 开发者工具，在网络面板（Network）中随便发送一条消息，捕获请求头中的 `Authorization` 字段值即为用户 Token。

## 🪜 使用 Proxy（网络代理）
> GPT、Midjourney 在国内网络环境下使用时需要配置代理访问。

* 详细的代理配置与转发流程，请参考：[使用代理请求 GPT、Midjourney 教程 Wiki](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E4%BD%BF%E7%94%A8%E4%BB%A3%E7%90%86%E8%AF%B7%E6%B1%82GPT%E3%80%81Midjourney)

## 📄 使用百度翻译及内容审核
> 用于对 GPT、Midjourney、Stable-Diffusion 的输入内容进行文本安全审核，并为画图提示词提供自动百度翻译能力。

1. 百度翻译接口申请与配置流程：[申请百度翻译 Wiki](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E7%94%B3%E8%AF%B7%E7%99%BE%E5%BA%A6%E7%BF%BB%E8%AF%91)
2. 百度文本内容审核申请与配置流程：[申请百度内容审核平台-文本 Wiki](https://github.com/a616567126/GPT-WEB-JAVA/wiki/%E7%94%B3%E8%AF%B7%E7%99%BE%E5%BA%A6%E5%86%85%E5%AE%B9%E5%AE%A1%E6%A0%B8%E5%B9%B3%E5%8F%B0-%E6%96%87%E6%9C%AC)

---

## 🥤 Reward（请作者喝杯冰可乐）

<table align="center">
    <tr>
        <td align="center" width="200px">
            <img src="https://user-images.githubusercontent.com/43660702/228105535-144d09cd-6326-4c22-b9b9-8c69c299caac.png" width="150px" height="150px"><br>
            <b>支付宝赞赏</b>
        </td>
        <td align="center" width="200px">
            <img src="https://user-images.githubusercontent.com/43660702/228105188-09c49078-9156-40bc-8327-f2b05c5bc5fa.png" width="150px" height="150px"><br>
            <b>微信赞赏</b>
        </td>
    </tr>
</table>

---

## ✉ 扫码添加好友（⭐ 记得点一个 Star 哦！）

<div align="center">
    <img src="https://user-images.githubusercontent.com/43660702/232187172-9d971a97-b7a3-407f-9ba1-a35516505733.jpeg" width="180px" alt="扫码添加好友"><br>
    <i>添加作者微信，咨询购买并加入专属 VIP 技术支持群</i>
</div>

---

### 📈 Star History

[![Star History Chart](https://star-history.dera.page/svg?repos=a616567126/GPT-WEB-JAVA&type=Timeline)](https://star-history.dera.page/#a616567126/GPT-WEB-JAVA&Timeline)

---

### 📝 License

[![Powered by DartNode](https://dartnode.com/branding/DN-Open-Source-sm.png)](https://dartnode.com "Powered by DartNode - Free VPS for Open Source")

本开源项目遵循 [Apache License 2.0](LICENSE) 协议。
