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
)

object Eulogy {
    /** 第 variant 篇悼词。先轮完这个品牌自己的句子，再轮通用模板，然后从头再来。 */
    fun write(packageName: String, brandId: String?, raw: RawFacts, variant: Int, zh: Boolean): String {
        val pack = if (zh) EulogyZh.pack else EulogyEn.pack
        val facts = pack.format(raw)
        val seed = packageName.hashCode() and 0x7fffffff
        val lines = pack.brandLines[brandId].orEmpty()
        val i = variant % (lines.size + pack.generic.size)
        return if (i < lines.size) {
            lines[(seed + i) % lines.size] + pack.joiner + pack.closings[(seed + variant) % pack.closings.size](facts)
        } else {
            pack.generic[(seed + i - lines.size) % pack.generic.size](facts)
        }
    }
}
