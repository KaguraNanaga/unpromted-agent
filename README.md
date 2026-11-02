[中文说明](README.zh-CN.md)

# Unprompted Agent

**Back to November 29, 2022.**

A small Android app for saying goodbye to the AI apps on your phone, one eulogy at a time. Then clean up the AI hype in your social feeds, by hand.

[![Build APK](https://github.com/KaguraNanaga/unpromted-agent/actions/workflows/build.yml/badge.svg)](https://github.com/KaguraNanaga/unpromted-agent/actions/workflows/build.yml)

**[Download the bilingual APK](https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/unprompted-v0.1.0-preview.apk)** · [Release notes](https://github.com/KaguraNanaga/unpromted-agent/releases/tag/v0.1.0) · [Report an issue](https://github.com/KaguraNanaga/unpromted-agent/issues)

## See it in action

<a href="https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/unprompted-demo.mp4"><img src="docs/assets/demo.gif" alt="Unprompted demo: scanning AI apps, changing eulogies, and opening the unfollow list" width="280"></a>

[29-second MP4](https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/unprompted-demo.mp4). The recording shows the Chinese interface with English captions. The app also includes a full English interface.

## Two pages, one way back

### Send off apps

- Find likely AI apps among the apps with launcher icons. Known packages are marked **Confirmed**; keyword matches are marked **Suspected**.
- Read a brand-specific eulogy, or tap **Another eulogy** for a different one.
- Tap **Send it off** to open Android's uninstall flow. You approve the uninstall yourself.
- System apps open their settings page so you can disable them where your phone allows it.
- Wrong guess? Tap **Spare it**. Spared apps are saved locally and skipped in later scans.

Detection uses local rules and can make mistakes. Review each match before uninstalling anything.

### The unfollow list

Find the familiar patterns: AI productivity gurus, prompt sellers, launch-day hype, and one-person-company sermons. Open a social app, unfollow accounts yourself, then return to record your progress.

The English list includes X, LinkedIn, Instagram, Threads, TikTok, YouTube, and Reddit. The Chinese list includes platforms such as Xiaohongshu, WeChat, Weibo, Douyin, and Bilibili. The app opens these platforms; it does not automate account actions.

## Install

1. Download **[unprompted-v0.1.0-preview.apk](https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/unprompted-v0.1.0-preview.apk)** from the public release. No GitHub account or Android Studio is needed to download it.
2. Open it on a phone running **Android 8.0 or later**.
3. When Android asks, allow installation from the browser or file manager you used.

This is a **debug-signed public preview**. If an earlier test build reports a signature conflict, uninstall that test build first. Uninstalling clears its local spared-app list and unfollow counter. It can coexist with the original “回到11.29” prototype because that prototype used a different package name.

The current application ID is `com.example.unprompted`. APK and video checksums are available in [SHA256SUMS.txt](https://github.com/KaguraNanaga/unpromted-agent/releases/download/v0.1.0/SHA256SUMS.txt).

## English and Chinese

The app follows your phone's system language: Chinese for `zh`, English otherwise. Switch the system language and reopen the app to try the other version.

The eulogies, jokes, and unfollow lists were written separately for each language. They are not just word-for-word translations.

## Local data

Scanning and eulogy selection happen on the device. The app does not request network access and has no account, analytics SDK, or model API. It stores only your spared-app choices and the unfollow counter in local preferences. Opening another app hands control to that app.

## Build without Android Studio

GitHub Actions builds a debug APK on pushes, pull requests, and manual runs.

1. Fork the repository or push your changes to it.
2. Open **Actions → Build APK**. To build manually, choose **Run workflow**.
3. Open the successful run and download **unprompted-debug-apk** from **Artifacts**. Actions artifact downloads require a GitHub login.
4. Extract `unprompted-debug.apk` from the ZIP. Artifacts are kept for 30 days; public release downloads are separate.

The project includes the complete Gradle Wrapper. The cloud build uses Java 17, Gradle 8.7, Android SDK 34, and Build Tools 34.0.0.

If you already have JDK 17 and an Android SDK installed, build locally with:

```sh
./gradlew :app:assembleDebug
```

On Windows, use `gradlew.bat :app:assembleDebug`. The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Change the rules or the copy

| File | What it controls |
| --- | --- |
| [`Detector.kt`](app/src/main/java/com/example/unprompted/Detector.kt) | Known packages, brand keywords, and broader AI detection rules |
| [`EulogyZh.kt`](app/src/main/java/com/example/unprompted/EulogyZh.kt) / [`EulogyEn.kt`](app/src/main/java/com/example/unprompted/EulogyEn.kt) | Chinese and English eulogy templates |
| [`Declutter.kt`](app/src/main/java/com/example/unprompted/Declutter.kt) | Platform links, account types, keywords, and milestones for each language |
| [`values/strings.xml`](app/src/main/res/values/strings.xml) / [`values-zh/strings.xml`](app/src/main/res/values-zh/strings.xml) | English and Chinese interface text |

## Acknowledgment

The initial app code was written with Claude. Yes, we see the irony.
