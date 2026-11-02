package com.example.unprompted

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** ChatGPT 发布于 2022 年 11 月 30 日，所以这是“蛮荒时代”的最后一天。 */
private val LAST_DAY: LocalDate = LocalDate.of(2022, 11, 29)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MemorialTheme { App() } }
    }
}

/** 送走 App 那一页的状态。放在页面外面，切到别的页再回来，扫描结果还在。 */
private class SendOffState(pardonedCount: Int) {
    var scanned by mutableStateOf(false)
    var found by mutableStateOf(emptyList<FoundApp>())
    val departed = mutableStateListOf<String>()
    var pending by mutableStateOf<FoundApp?>(null)
    var pardonedCount by mutableIntStateOf(pardonedCount)
}

@Composable
private fun App() {
    val context = LocalContext.current
    val store = remember { Store(context) }
    val state = remember { SendOffState(store.pardoned().size) }
    var page by remember { mutableIntStateOf(0) }

    fun rescan() {
        state.found = Scanner.scan(context, store.pardoned())
        state.pardonedCount = store.pardoned().size
        state.scanned = true
    }

    // 系统卸载框或设置页关掉后回到这里：看看它是不是真的走了
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        state.pending?.let { app ->
            if (Scanner.isGone(context, app.packageName) && app.label !in state.departed) {
                state.departed.add(app.label)
            }
        }
        state.pending = null
        rescan()
    }

    fun sendOff(app: FoundApp) {
        state.pending = app
        val uri = Uri.parse("package:${app.packageName}")
        // 系统自带的 App 卸不掉，只能去设置里停用
        val intent = if (app.isSystem) Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
        else Intent(Intent.ACTION_DELETE, uri)
        launcher.launch(intent)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Tabs(page, listOf("送走 App", "断舍离清单")) { page = it }
            Box(Modifier.weight(1f)) {
                when (page) {
                    0 -> SendOffPage(
                        state = state,
                        onScan = { rescan() },
                        onSendOff = { sendOff(it) },
                        onPardon = { store.pardon(it.packageName); rescan() },
                        onClearPardons = { store.clearPardons(); rescan() },
                    )
                    else -> DeclutterPage(store)
                }
            }
        }
    }
}

@Composable
private fun Tabs(selected: Int, titles: List<String>, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
        titles.forEachIndexed { i, title ->
            val on = i == selected
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelect(i) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = if (on) FontWeight.Bold else FontWeight.Normal,
                    color = if (on) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .width(28.dp)
                        .height(3.dp)
                        .background(if (on) MaterialTheme.colorScheme.primary else Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun SendOffPage(
    state: SendOffState,
    onScan: () -> Unit,
    onSendOff: (FoundApp) -> Unit,
    onPardon: (FoundApp) -> Unit,
    onClearPardons: () -> Unit,
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(24.dp),
    ) {
        item { Header() }

        if (!state.scanned) {
            item {
                Button(
                    onClick = onScan,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background,
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) { Text("找出手机里的 AI 软件", fontSize = 16.sp) }
            }
        } else if (state.found.isNotEmpty()) {
            item {
                Text(
                    "找到 ${state.found.size} 个。按下“送走它”后，系统会问你是否卸载，确定要你亲手按。认错了的，点“放过它”。",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
            }
            items(state.found, key = { it.packageName }) { app ->
                EulogyCard(app, onSendOff = { onSendOff(app) }, onPardon = { onPardon(app) })
            }
        } else {
            item { AllClear(nothingFoundAtAll = state.departed.isEmpty()) }
        }

        if (state.departed.isNotEmpty()) item { Departed(state.departed) }

        if (state.scanned && state.pardonedCount > 0) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "你放过了 ${state.pardonedCount} 个 App，扫描时会跳过它们。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onClearPardons) {
                        Text("全部撤销", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }

        if (state.scanned && state.found.isEmpty()) {
            item {
                OutlinedButton(
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DELETE, Uri.parse("package:${context.packageName}"))
                        )
                    },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) {
                    Text("最后，把我也送走", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun Header() {
    val days = ChronoUnit.DAYS.between(LAST_DAY, LocalDate.now())
    Column {
        Text(
            "2022.11.29",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Black,
            fontSize = 54.sp,
            lineHeight = 58.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "这一天，ChatGPT 还没有发布，AI 还没有住进你的手机。距今已经 $days 天。",
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 27.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            "人工不智能，帮你回到那一天。",
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 27.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EulogyCard(app: FoundApp, onSendOff: () -> Unit, onPardon: () -> Unit) {
    var variant by remember(app.packageName) { mutableIntStateOf(0) }
    val tag = when {
        app.kind == MatchKind.KNOWN -> "已确认"
        else -> "疑似：${app.reason}"
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(4.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                app.icon?.let {
                    // 遗像统一用黑白
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
                        modifier = Modifier.size(44.dp),
                    )
                    Spacer(Modifier.width(14.dp))
                }
                Column {
                    Text(
                        app.label,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(tag, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                Eulogy.write(app.packageName, app.brandId, app.facts, variant),
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                lineHeight = 27.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (app.isSystem) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "它是系统自带的，卸不掉。点下面的按钮会打开它的设置页，在那里点“停用”。",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onSendOff,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (app.isSystem) "去设置里停用" else "送走它", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Row {
                TextButton(onClick = { variant++ }) {
                    Text("换一篇悼词", color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onPardon) {
                    Text("放过它", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun AllClear(nothingFoundAtAll: Boolean) {
    val text = if (nothingFoundAtAll) {
        "一个 AI 软件都没找到。你可能本来就活在 2022 年。如果明明装了却没认出来，可以在 Detector.kt 里补上它的名字。"
    } else {
        "都送走了。你的手机回到了 2022 年 11 月 29 日。今天不会再有谁对你说“这是一个很好的问题”。"
    }
    Text(
        text,
        fontFamily = FontFamily.Serif,
        fontSize = 18.sp,
        lineHeight = 30.sp,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun Departed(names: List<String>) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            "已安息",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        names.forEach {
            Text(
                it,
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                lineHeight = 26.sp,
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
