package com.example.unprompted

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 断舍离清单：AI 没装在手机里，也会住在你的关注列表里。
 * 这一页不替你点任何东西，只告诉你去哪、找谁，然后把 App 打开。取关要你亲手按。
 */
private class Platform(val name: String, val packageName: String, val where: String)

/** 一种语言的整页内容。中英文的平台、账号类型和梗都不一样，所以分开写，不是翻译。 */
private class DeclutterCopy(
    val platforms: List<Platform>,
    val accountTypes: List<Pair<String, String>>,
    val keywords: List<String>,
    val keywordSeparator: String,
    val milestone: (Int) -> String?,
)

/** 包名按公开信息整理；入口位置随版本可能变化 */
private val zhCopy = DeclutterCopy(
    platforms = listOf(
        Platform("小红书", "com.xingin.xhs", "我 → 关注"),
        Platform("微信公众号", "com.tencent.mm", "通讯录 → 公众号，点进去 → 右上角 → 不再关注"),
        Platform("微博", "com.sina.weibo", "我 → 关注"),
        Platform("抖音", "com.ss.android.ugc.aweme", "我 → 关注"),
        Platform("哔哩哔哩", "tv.danmaku.bili", "我的 → 关注"),
        Platform("知乎", "com.zhihu.android", "我的 → 关注"),
        Platform("即刻", "com.ruguoapps.jike", "我的 → 关注"),
        Platform("X", "com.twitter.android", "个人主页 → Following"),
    ),
    accountTypes = listOf(
        "提效博主" to "“用 AI 一天干完一周的活”",
        "神器搬运号" to "“100 个 AI 神器，第 37 个绝了”",
        "焦虑贩卖号" to "“不会 AI 的人，正在被淘汰”",
        "提示词商贩" to "“万能提示词，评论区扣 1 领取”",
        "发布会复读机" to "每次有新模型，都是“炸裂”“颠覆”“变天了”",
        "副业导师" to "“零基础用 AI 月入过万”",
        "一人公司传教士" to "“一个人加十个 Agent，就是一家公司”",
        "养虾教程号" to "“三分钟装好龙虾，从此躺赚”",
    ),
    keywords = listOf(
        "AI", "Agent", "智能体", "龙虾", "提效", "干货", "炸裂", "颠覆", "变天", "封神", "保姆级",
        "提示词", "Prompt", "一人公司", "副业", "普通人的机会", "vibe coding", "全网首发", "建议收藏",
    ),
    keywordSeparator = "　",
    milestone = { n ->
        when {
            n >= 100 -> "一百个了。你的信息流回到了 2022 年。"
            n >= 50 -> "五十个了。算法开始怀疑你是不是换了一个人。"
            n >= 20 -> "二十个了。首页开始出现猫和饭了。"
            n >= 10 -> "十个了。首页开始变安静了。"
            n >= 1 -> "第一个最难。"
            else -> null
        }
    },
)

private val enCopy = DeclutterCopy(
    platforms = listOf(
        Platform("X", "com.twitter.android", "Profile → Following"),
        Platform("LinkedIn", "com.linkedin.android", "My Network → Following"),
        Platform("Instagram", "com.instagram.android", "Profile → Following"),
        Platform("Threads", "com.instagram.barcelona", "Profile → Following"),
        Platform("TikTok", "com.zhiliaoapp.musically", "Profile → Following"),
        Platform("YouTube", "com.google.android.youtube", "Subscriptions → All → Manage"),
        Platform("Reddit", "com.reddit.frontpage", "Communities → leave the AI ones"),
    ),
    accountTypes = listOf(
        "Thread guys" to "“I replaced my entire workflow with AI. Here’s how 🧵”",
        "Tool listicles" to "“10 AI tools that feel illegal to know”",
        "Fear sellers" to "“AI won’t take your job. Someone using AI will.”",
        "Prompt dealers" to "“Comment PROMPT and I’ll DM you my template”",
        "Launch-day echo chamber" to "Every new model: “This changes everything.” “It’s over.” “We’re so back.”",
        "Agent hustle gurus" to "“One person + 12 agents = a \$1M company”",
        "LinkedIn thought leaders" to "“I asked ChatGPT about my kid’s lemonade stand. Here’s what it taught me about B2B sales.”",
        "Lobster farmers" to "“Set up your own OpenClaw in 3 minutes and never work again”",
    ),
    keywords = listOf(
        "AI", "agent", "agentic", "🧵", "game-changer", "this changes everything", "10x", "unlock",
        "insane", "you’re falling behind", "vibe coding", "prompt", "AGI", "it’s over", "we’re so back", "RIP",
    ),
    keywordSeparator = "   ",
    milestone = { n ->
        when {
            n >= 100 -> "A hundred. Your feed is back in 2022."
            n >= 50 -> "Fifty. The algorithm suspects you’re a different person."
            n >= 20 -> "Twenty. Cats and food are showing up again."
            n >= 10 -> "Ten. Your feed is getting quieter."
            n >= 1 -> "The first one is the hardest."
            else -> null
        }
    },
)

@Composable
fun DeclutterPage(store: Store) {
    val context = LocalContext.current
    val copy = if (LocalConfiguration.current.locales[0].language == "zh") zhCopy else enCopy
    var count by remember { mutableIntStateOf(store.unfollowed()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(24.dp),
    ) {
        item {
            Column {
                Text(
                    stringResource(R.string.declutter_title),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    lineHeight = 46.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.declutter_intro),
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 27.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        item { SectionTitle(stringResource(R.string.declutter_types)) }
        items(copy.accountTypes) { (type, example) ->
            Column {
                Text(type, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    example,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            Column {
                SectionTitle(stringResource(R.string.declutter_keywords))
                Spacer(Modifier.height(8.dp))
                Text(
                    copy.keywords.joinToString(copy.keywordSeparator),
                    fontSize = 16.sp,
                    lineHeight = 30.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        item { SectionTitle(stringResource(R.string.declutter_where)) }
        items(copy.platforms, key = { it.packageName }) { p -> PlatformRow(context, p) }

        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(4.dp),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        context.resources.getQuantityString(R.plurals.unfollowed, count, count),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    copy.milestone(count)?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(it, fontFamily = FontFamily.Serif, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        stringResource(R.string.unfollow_hint),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = { count += 1; store.setUnfollowed(count) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(stringResource(R.string.btn_unfollowed_one), fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    if (count > 0) {
                        TextButton(onClick = { count = 0; store.setUnfollowed(0) }) {
                            Text(stringResource(R.string.reset), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 12.dp),
    )
}

@Composable
private fun PlatformRow(context: Context, p: Platform) {
    // 能看见带桌面图标的 App，所以这里拿得到启动入口；拿不到就是没装
    val launch = remember(p.packageName) { context.packageManager.getLaunchIntentForPackage(p.packageName) }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(p.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(p.where, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(12.dp))
        OutlinedButton(
            onClick = { launch?.let { context.startActivity(it) } },
            enabled = launch != null,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(stringResource(if (launch != null) R.string.open else R.string.not_installed), color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
