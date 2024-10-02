package com.example.nfc_task.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80,
//    surface = Color.Green
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8D4CD5),
    onPrimary = Color.White,

    secondary = Color(0xFFC481F3),
    onSecondary = Color.White,

    tertiary = Color(0xFFB9EBD3),
    onTertiary = Color.Black,

    background = Color.Red,
    onBackground = Color.Green,

    surface = Color(0xFFF5F5F5),
    surfaceBright = Color.White,
    surfaceDim = Color(0xFFE8E8E8),

    onSurface = Color.Black,
    onSurfaceVariant = Color(0xFF666666),

    primaryContainer = Color(0xFFB68BF5),
    onPrimaryContainer = Color.White,

    secondaryContainer = Color(0xFFCCAAF1),
    onSecondaryContainer = Color.White,

    tertiaryContainer = Color(0xFFF3E8F6),
    onTertiaryContainer = Color.Black,
)

@Composable
fun NFCTaskTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}