package com.example.unprompted

/**
 * 怎么判断一个 App 是不是 AI：三层规则，从严到宽。
 *
 * 1. 包名完全等于已知清单里的某一个 → “已确认”
 * 2. 包名或 App 名里出现某个品牌的关键词（qwen、grok、豆包……）→ “疑似”，归到那个品牌
 * 3. App 名里出现通用关键词（AI、GPT、智能体……）或包名里有 ai / gpt 这样的片段 → “疑似”
 *
 * 宽规则一定会误伤（比如叫 Gemini 的加密货币交易所、叫 Muse 的冥想 App、抓娃娃的 Claw 游戏），
 * 所以界面上有“放过它”。
 * 已知包名是按公开信息整理的，可能有错漏；就算包名不对，第 2 层通常也能兜住。
 */
data class Brand(
    val id: String,
    val name: String,
    val packages: List<String> = emptyList(),
    val packageHints: List<String> = emptyList(),
    val labelHints: List<String> = emptyList(),
)

enum class MatchKind { KNOWN, LIKELY }

data class Match(val brandId: String?, val kind: MatchKind, val reason: String)

object Detector {

    val brands: List<Brand> = listOf(
        Brand("chatgpt", "ChatGPT", listOf("com.openai.chatgpt"), listOf("openai"), listOf("ChatGPT", "OpenAI")),
        Brand("claude", "Claude", listOf("com.anthropic.claude"), listOf("anthropic"), listOf("Claude")),
        Brand("gemini", "Gemini", listOf("com.google.android.apps.bard"), listOf("apps.bard"), listOf("Gemini")),
        Brand("copilot", "Copilot", listOf("com.microsoft.copilot"), listOf("copilot"), listOf("Copilot")),
        Brand("grok", "Grok", listOf("ai.x.grok"), listOf("grok"), listOf("Grok")),
        Brand("perplexity", "Perplexity", listOf("ai.perplexity.app.android"), listOf("perplexity"), listOf("Perplexity")),
        Brand("character", "Character.AI", listOf("ai.character.app"), listOf("ai.character"), listOf("Character.AI", "Character AI")),
        Brand("metaai", "Meta AI", emptyList(), emptyList(), listOf("Meta AI")),
        Brand("lechat", "Le Chat", emptyList(), listOf("mistral"), listOf("Le Chat", "Mistral")),
        Brand("poe", "Poe", emptyList(), listOf("quora.poe"), listOf("Poe")),
        Brand("manus", "Manus", emptyList(), listOf("manus"), listOf("Manus")),
        Brand("mika", "Mika", emptyList(), listOf("mika"), listOf("Mika")),
        // 2026 年这一轮 personal agent
        Brand("muse", "Muse", emptyList(), listOf("meta.muse", "facebook.muse"), listOf("Muse")),
        Brand("openclaw", "OpenClaw", emptyList(), listOf("openclaw"), listOf("OpenClaw", "龙虾")),
        Brand("hermes", "Hermes Agent", emptyList(), listOf("nousresearch", "hermes.agent"), listOf("Hermes Agent")),
        Brand("qclaw", "QClaw", emptyList(), listOf("qclaw"), listOf("QClaw")),
        Brand("workbuddy", "WorkBuddy", emptyList(), listOf("workbuddy"), listOf("WorkBuddy")),
        Brand("copaw", "CoPaw", emptyList(), listOf("copaw"), listOf("CoPaw")),
        // 其余各种名字里带 Claw 的变种龙虾，放在具体品牌后面兜底
        Brand("claw", "龙虾", emptyList(), listOf("claw"), listOf("Claw")),
        Brand("deepseek", "DeepSeek", listOf("com.deepseek.chat"), listOf("deepseek"), listOf("DeepSeek", "深度求索")),
        Brand("doubao", "豆包", listOf("com.larus.nova"), listOf("doubao", "larus.nova"), listOf("豆包", "Doubao")),
        Brand("kimi", "Kimi", listOf("com.moonshot.kimichat"), listOf("moonshot", "kimi"), listOf("Kimi", "月之暗面")),
        Brand("qwen", "通义千问", listOf("com.aliyun.tongyi"), listOf("tongyi", "qwen", "qianwen"), listOf("通义", "千问", "Qwen")),
        Brand("wenxin", "文心一言", listOf("com.baidu.newapp"), listOf("wenxin", "yiyan"), listOf("文心", "ERNIE")),
        Brand("yuanbao", "腾讯元宝", listOf("com.tencent.hunyuan.app.chat"), listOf("hunyuan", "yuanbao"), listOf("元宝", "混元")),
        Brand("zhipu", "智谱清言", emptyList(), listOf("zhipu", "chatglm"), listOf("智谱", "清言", "ChatGLM")),
        Brand("hailuo", "海螺", emptyList(), listOf("hailuo", "minimax"), listOf("海螺", "MiniMax")),
        Brand("xinghuo", "讯飞星火", emptyList(), listOf("xinghuo"), listOf("星火")),
        Brand("tiangong", "天工", emptyList(), listOf("tiangong"), listOf("天工")),
        Brand("yuewen", "跃问", emptyList(), listOf("stepfun", "yuewen"), listOf("跃问", "阶跃")),
        Brand("metaso", "秘塔", emptyList(), listOf("metaso"), listOf("秘塔")),
    )

    /** 不属于任何品牌、但名字一看就和 AI 有关的 */
    private val genericLabelHints = listOf("AI", "GPT", "LLM", "Agent", "Chatbot", "智能体", "大模型", "人工智能")

    /** 包名按点切开后，某一段等于或包含这些 */
    private val genericPackageSegments = listOf("ai", "gpt", "llm", "chatbot", "agent")

    fun match(packageName: String, label: String): Match? {
        val pkg = packageName.lowercase()

        brands.firstOrNull { pkg in it.packages }?.let {
            return Match(it.id, MatchKind.KNOWN, "在已知清单里")
        }
        for (b in brands) {
            b.packageHints.firstOrNull { pkg.contains(it) }?.let {
                return Match(b.id, MatchKind.LIKELY, "包名里有「$it」")
            }
            b.labelHints.firstOrNull { labelHas(label, it) }?.let {
                return Match(b.id, MatchKind.LIKELY, "名字里有「$it」")
            }
        }
        genericLabelHints.firstOrNull { labelHas(label, it) }?.let {
            return Match(null, MatchKind.LIKELY, "名字里有「$it」")
        }
        val segments = pkg.split('.')
        genericPackageSegments.firstOrNull { hint ->
            segments.any { seg -> if (hint == "ai") seg == "ai" else seg.contains(hint) }
        }?.let {
            return Match(null, MatchKind.LIKELY, "包名里有「$it」")
        }
        return null
    }

    fun brandName(id: String?): String? = brands.firstOrNull { it.id == id }?.name

    /** 这些词容易是别的单词的开头（Manuscript、Poetry……），要求整词出现 */
    private val wholeWordOnly = setOf("AI", "LLM", "Poe", "Manus", "Mika", "Claude", "Kimi", "Muse")

    /** 出现在单词中间也算：ChatGPT、MyGPT、ArkClaw、NemoClaw…… */
    private val matchAnywhere = setOf("GPT", "Claw")

    /**
     * 英文关键词：前面不能紧贴字母或数字，免得 “MAIL” 被当成 “AI”。
     * 后面可以接别的字母，这样 “Grokbot”“QwenChat” 也能认出来；
     * wholeWordOnly 里的词前后都要隔开；matchAnywhere 里的词出现在哪都算。
     * 中文关键词直接看包不包含。
     */
    private fun labelHas(label: String, hint: String): Boolean {
        val latin = hint.all { it.code < 128 }
        if (!latin) return label.contains(hint)
        val word = Regex.escape(hint)
        val pattern = when {
            hint in matchAnywhere -> word
            hint in wholeWordOnly -> "(^|[^A-Za-z0-9])$word($|[^A-Za-z0-9])"
            else -> "(^|[^A-Za-z0-9])$word"
        }
        return Regex(pattern, RegexOption.IGNORE_CASE).containsMatchIn(label)
    }
}
