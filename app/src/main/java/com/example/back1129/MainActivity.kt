package com.example.back1129

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        setContent { MemorialTheme { MemorialScreen() } }
    }
}

@Composable
private fun MemorialScreen() {
    val context = LocalContext.current
    var scanned by remember { mutableStateOf(false) }
    var found by remember { mutableStateOf(emptyList<FoundApp>()) }
    val departed = remember { mutableStateListOf<String>() }
    var pending by remember { mutableStateOf<FoundApp?>(null) }

    // 系统卸载框关掉后回到这里：看看它是不是真的走了
    val uninstallLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        pending?.let { app ->
            if (!Scanner.isInstalled(context, app.packageName) && app.label !in departed) {
                departed.add(app.label)
            }
        }
        pending = null
        found = Scanner.scan(context)
    }

    fun sendOff(app: FoundApp) {
        pending = app
        uninstallLauncher.launch(Intent(Intent.ACTION_DELETE, Uri.parse("package:${app.packageName}")))
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
        ) {
            item { Header() }

            if (!scanned) {
                item {
                    Button(
                        onClick = { found = Scanner.scan(context); scanned = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.background,
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    ) { Text("找出手机里的 AI 软件", fontSize = 16.sp) }
                }
            } else if (found.isNotEmpty()) {
                item {
                    Text(
                        "找到 ${found.size} 位。按下“送走它”后，系统会问你是否卸载，确定要你亲手按。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                    )
                }
                items(found, key = { it.packageName }) { app -> EulogyCard(app) { sendOff(app) } }
            } else {
                item { AllClear(nothingFoundAtAll = departed.isEmpty()) }
            }

            if (departed.isNotEmpty()) {
                item { Departed(departed) }
            }

            if (scanned && found.isEmpty()) {
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
            "ChatGPT 发布的前一天。距今已经 $days 天。",
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 27.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            "这个 App 只做一件事：帮你回去。",
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 27.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EulogyCard(app: FoundApp, onSendOff: () -> Unit) {
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
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.colorMatrix(
                            androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) }
                        ),
                        modifier = Modifier.size(44.dp),
                    )
                    Spacer(Modifier.width(14.dp))
                }
                Text(
                    app.label,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                app.eulogy,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                lineHeight = 27.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onSendOff,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("送走它", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun AllClear(nothingFoundAtAll: Boolean) {
    val text = if (nothingFoundAtAll) {
        "清单上的 AI 软件一个都没找到。你可能本来就活在 2022 年，也可能是清单不全，可以在 AiApps.kt 里补上。"
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
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
