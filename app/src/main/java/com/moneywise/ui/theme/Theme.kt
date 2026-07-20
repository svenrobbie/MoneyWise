package com.moneywise.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val Teal40 = Color(0xFF009688)
private val Teal80 = Color(0xFF80CBC4)
private val Teal90 = Color(0xFFB2DFDB)

private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    secondary = Color(0xFFA7C0CD),
    tertiary = Color(0xFFA7CDB4),
    background = Color(0xFF1A1C1E),
    surface = Color(0xFF1A1C1E),
    onPrimary = Color(0xFF003730),
    onSecondary = Color(0xFF1B3340),
    onTertiary = Color(0xFF0B3820),
    onBackground = Color(0xFFE1E2E4),
    onSurface = Color(0xFFE1E2E4),
)

private val LightColorScheme = lightColorScheme(
    primary = Teal40,
    secondary = Color(0xFF4A6572),
    tertiary = Color(0xFF3A7D44),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun MoneyWiseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
