[English](README.md)

# 人工不智能 Unprompted Agent

**回到 2022 年 11 月 29 日。**

找出手机里的 AI 应用，为它们念一篇悼词，再由你亲手确认卸载。然后，去关注列表里做一次断舍离。

**[下载中英双语 APK](https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/unprompted-v0.1.0-preview.apk)** · [发布说明](https://github.com/KaguraNanaga/unpromted-agent/releases/tag/v0.1.0) · [反馈问题](https://github.com/KaguraNanaga/unpromted-agent/issues)

## 演示

<a href="https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/unprompted-demo.mp4"><img src="docs/assets/demo.gif" alt="人工不智能演示：扫描 AI 应用、切换悼词、查看断舍离清单" width="280"></a>

[观看或下载 29 秒 MP4](https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/unprompted-demo.mp4)。演示使用中文界面，并配有英文说明。

## 两个页面

**送走 App**：扫描有桌面图标的应用。已知包名标为“已确认”，关键词匹配标为“疑似”。可以切换悼词，点“送走它”打开系统卸载流程，最终确认由你操作。系统自带应用会打开设置页，在手机支持的情况下停用。识别错了可以点“放过它”，以后扫描会跳过。

**断舍离清单**：列出值得重新审视的 AI 账号类型、关键词和取关入口。打开社交平台后，取关由你亲手完成，再回到这里记录数量。中文版和英文版有各自的平台与文案。

识别规则可能误判，请核对后再决定是否卸载。应用只打开其他平台，不会自动操作你的账号。

## 安装

1. 下载 [unprompted-v0.1.0-preview.apk](https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/unprompted-v0.1.0-preview.apk)，无需 GitHub 账号，也不需要 Android Studio。
2. 在安卓 8.0 或更高版本的手机上打开 APK。
3. 手机提示时，允许当前浏览器或文件管理器“安装未知应用”。

这是使用调试证书签名的**公开预览版**。如果之前的测试版提示签名冲突，需要先卸载旧测试版；卸载会清除本地“放过它”清单和取关计数。最初的“回到11.29”使用不同包名，可以与本版共存。

当前包名为 `com.example.unprompted`。[SHA256SUMS.txt](https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/SHA256SUMS.txt) 提供 APK 和视频的校验值。

## 中英文切换

应用跟随系统语言：中文系统显示中文，其他语言默认显示英文。想测试英文版，把手机语言改为英文，再重新打开应用即可。

界面文字、悼词、梗和断舍离清单都有对应内容，并非逐字翻译。

## 本地数据

扫描和悼词选择都在手机本地完成。应用不申请网络权限，没有登录、统计 SDK 或模型 API，只在本地保存“放过它”清单和取关计数。打开其他应用后，操作交由该应用处理。

## 不装 Android Studio，用 GitHub 编译

项目包含完整的 Gradle Wrapper 和 GitHub Actions 构建配置。提交代码、创建拉取请求或手动运行时，云端会生成测试版 APK。

1. Fork 仓库，或将更改推送到仓库。
2. 打开 **Actions → Build APK**；手动构建可点击 **Run workflow**。
3. 在成功运行的底部 **Artifacts** 下载 **unprompted-debug-apk**。下载 Actions 产物需要登录 GitHub。
4. 解压 ZIP，得到 `unprompted-debug.apk`。构建产物保留 30 天，与公开 Release 下载相互独立。

云端使用 Java 17、Gradle 8.7、Android SDK 34 和 Build Tools 34.0.0。

如已安装 JDK 17 和 Android SDK，可运行 `./gradlew :app:assembleDebug`；Windows 使用 `gradlew.bat :app:assembleDebug`。输出位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 修改内容

- [`Detector.kt`](app/src/main/java/com/example/unprompted/Detector.kt)：已知包名、品牌关键词和通用识别规则。
- [`EulogyZh.kt`](app/src/main/java/com/example/unprompted/EulogyZh.kt)、[`EulogyEn.kt`](app/src/main/java/com/example/unprompted/EulogyEn.kt)：中英文悼词。
- [`Declutter.kt`](app/src/main/java/com/example/unprompted/Declutter.kt)：各语言的平台、账号类型、关键词和计数文案。
- [`values/strings.xml`](app/src/main/res/values/strings.xml)、[`values-zh/strings.xml`](app/src/main/res/values-zh/strings.xml)：中英文界面文字。

## 致谢

本项目的初版代码由 Claude 编写。我们知道这很讽刺。
