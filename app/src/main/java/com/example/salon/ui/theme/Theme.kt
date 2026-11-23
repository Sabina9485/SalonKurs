package com.example.salon.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Палитра салона: сливовый и малиновый градиент, нежно-розовые карточки, белый «лист»
val Plum = Color(0xFF7A0F7A)
val Magenta = Color(0xFFA4208F)
val Orchid = Color(0xFFD27BB4)
val Blush = Color(0xFFFFE3EC)
val Petal = Color(0xFFFBF1F6)
val BgColor = Color.White
val Ink = Color(0xFF3A1B3A)
val Muted = Color(0xFF9A8396)
val Line = Color(0xFFEBD9E4)
val Coral = Color(0xFFE5484D)
val Green = Color(0xFF2E9E3F)
val Amber = Color(0xFFF59E0B)
val Violet = Color(0xFF8E5CF7)

val HeaderGradient = Brush.horizontalGradient(listOf(Color(0xFF8A1A8C), Color(0xFFD27BB4)))
val PillGradient = Brush.horizontalGradient(listOf(Color(0xFF7D0B7D), Color(0xFFF6A9C6)))

private val Scheme = lightColorScheme(
    primary = Plum, onPrimary = Color.White, secondary = Orchid, onSecondary = Color.White,
    background = Color.White, onBackground = Ink, surface = Color.White, onSurface = Ink,
    surfaceVariant = Petal, onSurfaceVariant = Muted, outline = Line, error = Coral,
    primaryContainer = Blush, onPrimaryContainer = Plum,
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color.White, surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White, surfaceContainerHighest = Petal
)

@Composable
fun AuraTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme) {
        CompositionLocalProvider(LocalContentColor provides Ink, content = content)
    }
}
