package com.example.back1129

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 灵堂配色：素白、墨黑，加一点菊花黄，只用在“送走它”这个动作上。
val Chrysanthemum = Color(0xFFE2B007)

private val Light = lightColorScheme(
    background = Color(0xFFE9EAE8),   // 素
    surface = Color(0xFFFFFFFF),      // 白
    onBackground = Color(0xFF000000), // 墨
    onSurface = Color(0xFF000000),
    onSurfaceVariant = Color(0xFF5E605D),
    outline = Color(0xFF000000),
    primary = Chrysanthemum,
    onPrimary = Color(0xFF000000),
)

private val Dark = darkColorScheme(
    background = Color(0xFF000000),
    surface = Color(0xFF151615),
    onBackground = Color(0xFFEDEEEC),
    onSurface = Color(0xFFEDEEEC),
    onSurfaceVariant = Color(0xFF9A9C98),
    outline = Color(0xFF6A6C68),
    primary = Chrysanthemum,
    onPrimary = Color(0xFF000000),
)

@Composable
fun MemorialTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
