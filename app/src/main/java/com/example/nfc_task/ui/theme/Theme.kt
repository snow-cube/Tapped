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
    primary = purple1,
    onPrimary = Color.White,

    secondary = purple2,
    onSecondary = Color.White,

    tertiary = lightGreen,
    onTertiary = Color.White,

    background = Color.White,
    onBackground = Color.Black,

    surface = lightGrey,
    onSurface = Color.Black,

    primaryContainer = purple1,
    onPrimaryContainer = Color.White,

    secondaryContainer = purple2,
    onSecondaryContainer = Color.White,

    tertiaryContainer = purple3,
    onTertiaryContainer = Color.Black,
)

@Composable
fun NFCTaskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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