package com.example.back1129

/**
 * 要送走的 AI 软件清单：包名 to 显示名。
 *
 * 注意：包名是按已知信息整理的，可能有错漏，发布前请在自己手机上核对
 * （设置 → 应用 → 应用详情，或 adb shell pm list packages）。
 * 新增一项时，记得同时在 AndroidManifest.xml 的 <queries> 里加上同一个包名，
 * 否则安卓 11 以上的系统会把它“藏起来”，扫描不到。
 */
object AiApps {
    val known: List<Pair<String, String>> = listOf(
        "com.openai.chatgpt" to "ChatGPT",
        "com.anthropic.claude" to "Claude",
        "com.google.android.apps.bard" to "Gemini",
        "com.microsoft.copilot" to "Copilot",
        "ai.perplexity.app.android" to "Perplexity",
        "ai.x.grok" to "Grok",
        "ai.character.app" to "Character.AI",
        "com.deepseek.chat" to "DeepSeek",
        "com.larus.nova" to "豆包",
        "com.moonshot.kimichat" to "Kimi",
        "com.aliyun.tongyi" to "通义",
        "com.baidu.newapp" to "文心一言",
        "com.tencent.hunyuan.app.chat" to "腾讯元宝",
    )
}
