package com.example.unprompted

/**
 * 悼词：每个品牌有自己的几句，再加一批通用模板和梗，可以一直“换一篇”。
 * 中文和英文各写一套（EulogyZh.kt、EulogyEn.kt），按手机语言选。
 * 故意不用大模型生成：送走 AI 的悼词，不该由 AI 来写。
 */
class RawFacts(val name: String, val firstInstallTime: Long, val apkBytes: Long) {
    fun days(): Long = (System.currentTimeMillis() - firstInstallTime) / 86_400_000L
}

/** 按语言格式化好的“生平” */
class Facts(val name: String, val born: String, val age: String, val size: String)

class EulogyPack(
    val brandLines: Map<String, List<String>>,
    val closings: List<(Facts) -> String>,
    val generic: List<(Facts) -> String>,
    val format: (RawFacts) -> Facts,
    /** 品牌句和身后事之间：中文直接接上，英文空一格 */
    val joiner: String,
    /** 替你干活的那一类 AI 共用的句子：订酒店、发邮件、比价、早报……送它们去放个假 */
    val agentLines: List<String> = emptyList(),
)

/** 这些品牌会额外抽到 agentLines。现在的聊天助手几乎都长出了 agent 功能，所以大部分都算。 */
val AGENT_BRANDS = setOf(
    "chatgpt", "claude", "gemini", "copilot", "grok", "perplexity", "doubao",
    "muse", "duet", "grokbot", "openclaw", "hermes", "manus", "mika", "qclaw", "workbuddy", "copaw", "claw",
    "alexa", "neon", "hark", "lindy", "poke", "genspark", "devin", "nanobot",
)

object Eulogy {
    /** 第 variant 篇悼词，轮完一圈从头再来。 */
    fun write(packageName: String, brandId: String?, raw: RawFacts, variant: Int, zh: Boolean): String {
        val pack = if (zh) EulogyZh.pack else EulogyEn.pack
        val facts = pack.format(raw)
        val seed = packageName.hashCode() and 0x7fffffff
        // 顺序：先轮这个品牌自己的句子，再轮 agent 共用的，最后是通用模板
        val own = pack.brandLines[brandId].orEmpty()
        val agent = if (brandId in AGENT_BRANDS) pack.agentLines else emptyList()
        val i = variant % (own.size + agent.size + pack.generic.size)
        val closing = pack.closings[(seed + variant) % pack.closings.size](facts)
        return when {
            i < own.size -> own[(seed + i) % own.size] + pack.joiner + closing
            i < own.size + agent.size -> agent[(seed + i - own.size) % agent.size] + pack.joiner + closing
            else -> pack.generic[(seed + i - own.size - agent.size) % pack.generic.size](facts)
        }
    }
}
