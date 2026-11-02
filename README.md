# 人工不智能 Unprompted

手机语言是中文时是“人工不智能”，其他语言是英文版“Unprompted”。两个版本不是互相翻译的：悼词、梗和断舍离清单各写了一套。

- 界面文字：`res/values/strings.xml`（英文，默认）和 `res/values-zh/strings.xml`（中文）
- 悼词：`EulogyZh.kt`、`EulogyEn.kt`
- 断舍离清单：`Declutter.kt` 里的 `zhCopy`、`enCopy`。英文版列的是 X、LinkedIn、Instagram、Threads、TikTok、YouTube、Reddit

想看英文版，把手机系统语言切到英文再打开 App 就行。

2022 年 11 月 30 日，ChatGPT 发布。这个安卓 App 帮你回到它发布的前一天：2022 年 11 月 29 日，AI 还没住进你手机的最后一天。

它有两页。

**送走 App**：找出手机里的 AI 软件，给每一个念一段悼词，然后调起系统的卸载确认框。按下“确定”的永远是你自己。系统自带、卸不掉的（比如有些手机预装的 Gemini），会带你去设置页停用。全部送走之后，它会请你把它也卸载掉。

**断舍离清单**：AI 不只装在手机里，也住在你的关注列表里。这一页列出该取关的账号类型和关键词，一键打开小红书、微信、微博等 App，由你亲手取关，回来给自己记一笔。它不替你点任何东西，所以不会碰到这些平台的自动化封号规则。

## 不装 Android Studio，用 GitHub 编译

项目已包含完整的 Gradle Wrapper 和经过验证的 GitHub Actions 构建配置，电脑无需安装 Android Studio、Java 或安卓 SDK。

1. 将此目录中的文件上传到 GitHub 仓库根目录，保留 `.github` 和 `gradle` 目录。根目录应直接包含 `settings.gradle.kts`、`gradlew` 和 `app`。
2. 每次提交代码都会自动构建。也可以打开 **Actions → Build APK → Run workflow** 手动运行。
3. 等本次运行显示绿色勾号，在运行详情底部 **Artifacts** 下载 **unprompted-debug-apk**，或点击摘要中的“下载测试版 APK”。下载需要登录有权访问仓库的 GitHub 账号。
4. 解压 ZIP，将 `unprompted-debug.apk` 传到安卓 8.0 或更高版本的手机，打开安装；提示时允许当前浏览器或文件管理器“安装未知应用”。

产物保留 30 天，过期后可重新构建。这是使用调试证书签名的测试版，正式发布前需配置自己的发布签名。不同云端构建可能使用不同调试证书，如提示签名冲突，需要先卸载旧测试版再安装。

此版使用 `com.example.unprompted` 包名，与最初的“回到11.29”（`com.example.back1129`）作为两个独立应用共存。

## 用 Android Studio 编译

打开这个文件夹，等 Gradle 同步完，连上手机点运行。要求安卓 8.0 及以上。

## 怎么认出 AI 软件

规则都在 `Detector.kt`，分三层：

1. 包名在已知清单里，标为“已确认”。
2. 包名或 App 名里有某个品牌的关键词（qwen、grok、豆包、元宝……），标为“疑似”，并归到那个品牌，用那个品牌专属的悼词。
3. App 名里有通用关键词（AI、GPT、Agent、智能体、大模型……），或包名里有 ai、gpt 这样的片段，也标为“疑似”。

除了各家聊天助手，也覆盖了 2026 年这一轮 personal agent：Meta 的 Muse、OpenClaw 和它的各种变种龙虾（QClaw、WorkBuddy、CoPaw，以及名字里带 Claw 的其他 App）、Hermes Agent，Gemini Spark、Microsoft Scout 这类住在已有 App 里的，则写进了对应品牌的悼词。

宽规则一定会误伤，比如叫 Gemini 的加密货币交易所、叫 Muse 的冥想 App、抓娃娃的 Claw 游戏。每张卡片上都有“放过它”，放过的 App 存在本机，以后扫描会跳过。

App 能看见所有带桌面图标的应用，靠的是在 `AndroidManifest.xml` 里声明对桌面启动入口的查询，不需要 QUERY_ALL_PACKAGES 权限。

## 改断舍离清单

账号类型、关键词和平台都在 `Declutter.kt` 开头。平台的包名和“去哪里取关”的路径按公开信息整理，App 改版后可能变化。

## 改悼词

悼词在 `EulogyZh.kt` 和 `EulogyEn.kt`：每个品牌有自己的几句（包括各家的梗，比如 Claude 的 You're absolutely right、Grok 的 @grok is this true），后面接一句身后事；另有一批通用模板和全行业通用的梗（9.11 和 9.9、strawberry 里的 r、骑自行车的鹈鹕、林黛玉倒拔垂杨柳……）。卡片上的“换一篇悼词”会依次轮换。它们故意不用大模型生成：送走 AI 的悼词，不该由 AI 来写。

## 发布前

把 `com.example.unprompted` 换成你自己的包名（`app/build.gradle.kts` 里的 `namespace` 和 `applicationId`，以及源码目录）。

## 声明

本项目的初版代码由 Claude 编写。我们知道这很讽刺。

## English, briefly

Unprompted is an Android app that takes you back to November 29, 2022, the day before ChatGPT launched. It finds the AI apps on your phone, reads each one a eulogy, and opens Android’s uninstall prompt so you can send it off yourself. A second page lists the kinds of AI accounts worth unfollowing and opens X, LinkedIn, Instagram and the rest so you can do it by hand. When everything is gone, it asks you to uninstall it too.
