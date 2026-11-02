# 回到 11.29

ChatGPT 发布于 2022 年 11 月 30 日。这个安卓 App 帮你回到它发布的前一天。

它做三件事：找出手机里的 AI 软件，给每一个念一段悼词，然后调起系统的卸载确认框。按下“确定”的永远是你自己。全部送走之后，它会请你把它也卸载掉。

## 不装 Android Studio，用 GitHub 编译

项目已包含 `.github/workflows/build.yml` 和完整的 Gradle Wrapper。电脑无需安装 Android Studio、Java 或安卓 SDK，构建环境由 GitHub 云端提供。

1. 将此目录中的项目文件上传到 GitHub 仓库根目录，保留 `.github` 和 `gradle` 目录。仓库根目录应直接包含 `settings.gradle.kts`、`gradlew` 和 `app`。
2. 打开仓库的 **Actions → Build APK**。每次提交代码都会自动构建；也可以点击 **Run workflow** 手动运行。
3. 等待本次运行显示绿色勾号，打开运行详情，在底部 **Artifacts** 下载 **back1129-debug-apk**，或点击摘要中的“下载测试版 APK”。下载需要登录有权访问仓库的 GitHub 账号。
4. 解压下载的 ZIP，将 `back1129-debug.apk` 传到手机并打开。手机提示时，允许当前浏览器或文件管理器“安装未知应用”。

要求：安卓 8.0 及以上。构建产物保留 30 天，过期后可重新运行工作流生成。

这是已使用调试证书签名的测试版，可直接安装；正式发布前需要配置自己的发布签名。不同云端构建可能使用不同调试证书，如果手机提示签名冲突，需要先卸载旧测试版再安装。

云端使用 Java 17、Gradle 8.7、Android SDK 34 和 Build Tools 34.0.0，与项目的 Android Gradle Plugin 8.5.2 配套。

## 可选：本地构建

已安装 JDK 17 和 Android SDK 的情况下，可在项目根目录运行 `./gradlew :app:assembleDebug`；Windows 使用 `gradlew.bat :app:assembleDebug`。输出位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 改清单

要送走的 App 列在 `app/src/main/java/com/example/back1129/AiApps.kt`。新增一项时，要在 `AndroidManifest.xml` 的 `<queries>` 里加上同一个包名，否则安卓 11 以上扫描不到。

清单里的包名是按已知信息整理的，可能有错漏，发布前请在真机上核对：`adb shell pm list packages | grep 关键词`。

## 改悼词

悼词模板在 `Eulogy.kt`。它们故意不用大模型生成：送走 AI 的悼词，不该由 AI 来写。

## 发布前

- 把 `com.example.back1129` 换成你自己的包名（`app/build.gradle.kts` 里的 `namespace` 和 `applicationId`，以及源码目录）。
- 这个版本没有自定义图标，用的是系统默认图标。

## 声明

本项目的初版代码由 Claude 编写。我们知道这很讽刺。
