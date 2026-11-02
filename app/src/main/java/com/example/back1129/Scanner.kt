package com.example.back1129

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import java.io.File

data class FoundApp(
    val packageName: String,
    val label: String,
    val icon: ImageBitmap?,
    val eulogy: String,
)

object Scanner {

    fun scan(context: Context): List<FoundApp> {
        val pm = context.packageManager
        return AiApps.known.mapNotNull { (pkg, fallbackName) ->
            val info = try {
                pm.getPackageInfo(pkg, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            } ?: return@mapNotNull null
            val appInfo = info.applicationInfo ?: return@mapNotNull null

            val label = appInfo.loadLabel(pm).toString().ifBlank { fallbackName }
            val icon = runCatching {
                appInfo.loadIcon(pm).toBitmap(width = 96, height = 96).asImageBitmap()
            }.getOrNull()
            val apkBytes = runCatching { File(appInfo.sourceDir).length() }.getOrDefault(0L)

            FoundApp(
                packageName = pkg,
                label = label,
                icon = icon,
                eulogy = Eulogy.write(pkg, label, info.firstInstallTime, apkBytes),
            )
        }
    }

    fun isInstalled(context: Context, pkg: String): Boolean = try {
        context.packageManager.getPackageInfo(pkg, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
