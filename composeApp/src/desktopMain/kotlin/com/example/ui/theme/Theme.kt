package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

private val DarkColorScheme = darkColorScheme(
    primary = WitcherAmberGold,
    secondary = WitcherRedPrimary,
    tertiary = WitcherRedSecondary,
    background = WitcherDarkBackground,
    surface = WitcherDarkSurface,
    surfaceVariant = WitcherDarkSurfaceVariant,
    onPrimary = WitcherDarkBackground,
    onSecondary = WitcherWhiteText,
    onBackground = WitcherWhiteText,
    onSurface = WitcherWhiteText,
    onSurfaceVariant = WitcherMutedText
)

// A fallback light theme that still preserves the Witcher red/amber accents
private val LightColorScheme = lightColorScheme(
    primary = WitcherRedPrimary,
    secondary = WitcherAmberGold,
    tertiary = WitcherRedSecondary,
    background = WitcherWhiteText,
    surface = WitcherWhiteText,
    onPrimary = WitcherWhiteText,
    onSecondary = WitcherDarkBackground,
    onBackground = WitcherDarkBackground,
    onSurface = WitcherDarkBackground
)

fun Modifier.parchmentBackground(): Modifier = this.drawBehind {
    val brush = Brush.linearGradient(
        colors = listOf(WitcherDarkSurface, WitcherDarkSurfaceVariant),
        start = Offset(0f, 0f),
        end = Offset(size.width, size.height)
    )
    drawRect(brush)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme by default for immersive Witcher vibes
    dynamicColor: Boolean = false, // Set to false to enforce our handcrafted Witcher design
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
