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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
            Tabs(page, listOf(stringResource(R.string.tab_send_off), stringResource(R.string.tab_declutter))) { page = it }
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
                ) { Text(stringResource(R.string.scan), fontSize = 16.sp) }
            }
        } else if (state.found.isNotEmpty()) {
            item {
                Text(
                    context.resources.getQuantityString(R.plurals.found, state.found.size, state.found.size),
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
                        context.resources.getQuantityString(R.plurals.spared, state.pardonedCount, state.pardonedCount),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onClearPardons) {
                        Text(stringResource(R.string.undo_all), color = MaterialTheme.colorScheme.onBackground)
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
                    Text(stringResource(R.string.send_me_off), color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
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
            stringResource(R.string.header_sub, days.toInt()),
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 27.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            stringResource(R.string.header_tag),
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
    val zh = LocalConfiguration.current.locales[0].language == "zh"
    val tag = when (app.reason) {
        Reason.KNOWN_PACKAGE -> stringResource(R.string.tag_known)
        Reason.PACKAGE_HINT -> stringResource(R.string.tag_package_hint, app.hint)
        Reason.LABEL_HINT -> stringResource(R.string.tag_label_hint, app.hint)
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
                Eulogy.write(app.packageName, app.brandId, app.facts, variant, zh),
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                lineHeight = 27.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (app.isSystem) {
                Spacer(Modifier.height(10.dp))
                Text(
                    stringResource(R.string.system_note),
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
            ) { Text(stringResource(if (app.isSystem) R.string.btn_disable else R.string.btn_send_off), fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Row {
                TextButton(onClick = { variant++ }) {
                    Text(stringResource(R.string.btn_another), color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onPardon) {
                    Text(stringResource(R.string.btn_spare), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun AllClear(nothingFoundAtAll: Boolean) {
    val text = stringResource(if (nothingFoundAtAll) R.string.all_clear_none else R.string.all_clear_done)
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
            stringResource(R.string.departed_title),
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
