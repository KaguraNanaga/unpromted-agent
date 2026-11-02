package com.example.unprompted

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import java.io.File

data class FoundApp(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap?,
    val brandId: String?,
    val kind: MatchKind,
    val reason: Reason,
    val hint: String,
    val isSystem: Boolean,
    val facts: RawFacts,
)

object Scanner {

    /** 扫描所有带桌面图标的 App，挑出像 AI 的那些。pardoned 里的跳过。 */
    fun scan(context: Context, pardoned: Set<String>): List<FoundApp> {
        val pm = context.packageManager
        val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val packages = pm.queryIntentActivities(launcher, 0)
            .map { it.activityInfo.packageName }
            .distinct()
            .filter { it != context.packageName && it !in pardoned }

        return packages.mapNotNull { pkg ->
            val info = try {
                pm.getPackageInfo(pkg, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            } ?: return@mapNotNull null
            val appInfo = info.applicationInfo ?: return@mapNotNull null
            val label = appInfo.loadLabel(pm).toString()
            val match = Detector.match(pkg, label) ?: return@mapNotNull null

            val icon = runCatching {
                appInfo.loadIcon(pm).toBitmap(width = 96, height = 96).asImageBitmap()
            }.getOrNull()
            val apkBytes = runCatching { File(appInfo.sourceDir).length() }.getOrDefault(0L)

            FoundApp(
                packageName = pkg,
                label = label,
                icon = icon,
                brandId = match.brandId,
                kind = match.kind,
                reason = match.reason,
                hint = match.hint,
                isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                facts = RawFacts(label, info.firstInstallTime, apkBytes),
            )
        }.sortedWith(compareBy({ it.kind != MatchKind.KNOWN }, { it.label }))
    }

    /** 卸载了，或者被停用了，都算走了 */
    fun isGone(context: Context, pkg: String): Boolean = try {
        !context.packageManager.getApplicationInfo(pkg, 0).enabled
    } catch (e: PackageManager.NameNotFoundException) {
        true
    }
}
