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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 断舍离清单：AI 没装在手机里，也会住在你的关注列表里。
 * 这一页不替你点任何东西，只告诉你去哪、找谁，然后把 App 打开。取关要你亲手按。
 */
private class Platform(val name: String, val packageName: String, val where: String)

/** 包名按公开信息整理；入口位置随版本可能变化 */
private val platforms = listOf(
    Platform("小红书", "com.xingin.xhs", "我 → 关注"),
    Platform("微信公众号", "com.tencent.mm", "通讯录 → 公众号，点进去 → 右上角 → 不再关注"),
    Platform("微博", "com.sina.weibo", "我 → 关注"),
    Platform("抖音", "com.ss.android.ugc.aweme", "我 → 关注"),
    Platform("哔哩哔哩", "tv.danmaku.bili", "我的 → 关注"),
    Platform("知乎", "com.zhihu.android", "我的 → 关注"),
    Platform("即刻", "com.ruguoapps.jike", "我的 → 关注"),
    Platform("X", "com.twitter.android", "个人主页 → Following"),
)

private val accountTypes = listOf(
    "提效博主" to "“用 AI 一天干完一周的活”",
    "神器搬运号" to "“100 个 AI 神器，第 37 个绝了”",
    "焦虑贩卖号" to "“不会 AI 的人，正在被淘汰”",
    "提示词商贩" to "“万能提示词，评论区扣 1 领取”",
    "发布会复读机" to "每次有新模型，都是“炸裂”“颠覆”“变天了”",
    "副业导师" to "“零基础用 AI 月入过万”",
    "一人公司传教士" to "“一个人加十个 Agent，就是一家公司”",
)

private val keywords = listOf(
    "AI", "Agent", "智能体", "提效", "干货", "炸裂", "颠覆", "变天", "封神", "保姆级",
    "提示词", "Prompt", "一人公司", "副业", "普通人的机会", "vibe coding", "全网首发", "建议收藏",
)

/** 每到一个数，说一句话 */
private fun milestone(n: Int): String? = when {
    n >= 100 -> "一百个了。你的信息流回到了 2022 年。"
    n >= 50 -> "五十个了。算法开始怀疑你是不是换了一个人。"
    n >= 20 -> "二十个了。首页开始出现猫和饭了。"
    n >= 10 -> "十个了。首页开始变安静了。"
    n >= 1 -> "第一个最难。"
    else -> null
}

@Composable
fun DeclutterPage(store: Store) {
    val context = LocalContext.current
    var count by remember { mutableIntStateOf(store.unfollowed()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(24.dp),
    ) {
        item {
            Column {
                Text(
                    "断舍离清单",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    lineHeight = 46.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "AI 没装在你手机里，也会住在你的关注列表里。打开下面的 App，找到这些账号，亲手取关。",
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 27.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        item { SectionTitle("这几类，可以走了") }
        items(accountTypes) { (type, example) ->
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
                SectionTitle("名字或简介里有这些词的，多半是")
                Spacer(Modifier.height(8.dp))
                Text(
                    keywords.joinToString("　"),
                    fontSize = 16.sp,
                    lineHeight = 30.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        item { SectionTitle("去哪里取关") }
        items(platforms, key = { it.packageName }) { p -> PlatformRow(context, p) }

        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(4.dp),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "已亲手取关 $count 个",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    milestone(count)?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(it, fontFamily = FontFamily.Serif, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "每取关一个，回来点一下。没人检查，全凭良心。",
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
                    ) { Text("又取关了一个", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    if (count > 0) {
                        TextButton(onClick = { count = 0; store.setUnfollowed(0) }) {
                            Text("清零", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            Text(if (launch != null) "打开" else "没装", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
