package com.example.back1129

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/** 用模板写悼词。故意不用大模型：送走 AI 的悼词，不该由 AI 来写。 */
object Eulogy {

    private class Facts(val name: String, val born: String, val age: String, val size: String)

    private val templates: List<(Facts) -> String> = listOf(
        { f -> "${f.name}，生于${f.born}，享年${f.age}。生前占用你${f.size}的存储空间，回答过许多你本可以自己想明白的问题。愿它在云端安息。" },
        { f -> "${f.age}前，你把${f.name}请进了手机。它从不拒绝你，也从不说“我不知道”。有时候，这正是问题所在。" },
        { f -> "${f.name}（${f.born} – 今天）。它替你写过邮件、改过代码、起过名字。从今往后，这些事你要自己来了。别怕，2022 年的人都是这么过的。" },
        { f -> "这里躺着${f.name}。体重${f.size}，在你的手机里住了${f.age}，一次房租也没交过。" },
        { f -> "${f.name}走了。走得很安详，没有幻觉，也没有超出上下文长度。享年${f.age}。" },
        { f -> "沉痛悼念${f.name}。每一次你提问，它都说“这是一个很好的问题”。安息吧，你的每个问题也都很好。" },
    )

    fun write(packageName: String, name: String, firstInstallTime: Long, apkBytes: Long): String {
        val born = SimpleDateFormat("yyyy年M月d日", Locale.CHINA).format(Date(firstInstallTime))
        val days = (System.currentTimeMillis() - firstInstallTime) / 86_400_000L
        val age = if (days < 1) "不到一天" else "${days}天"
        val size = if (apkBytes > 0) String.format(Locale.CHINA, "%.1f MB", apkBytes / 1_048_576.0) else "未知大小"
        // 同一个 App 每次都拿到同一篇悼词，不会越扫越乱
        val template = templates[abs(packageName.hashCode()) % templates.size]
        return template(Facts(name, born, age, size))
    }
}
