package me.snowcube.tapped.ui.theme

import androidx.compose.ui.graphics.Color

data class PaletteColor(
    val normalGreen: Color,
    val darkGreen: Color,

    val normalRed: Color,

    val normalBlue: Color,

    val normalYellow: Color,
    val darkYellow: Color,

    val backgroundGreen: Color,
    val backgroundYellow: Color,
    val backgroundBlue: Color,

    val invalidGrey: Color,
)

val paletteColor = PaletteColor(
    normalGreen = Color(0xFF9CE0B3),
    darkGreen = Color(0xFF1A571A),

    normalRed = Color(0xFFF38181),

    normalBlue = Color(0xFF8EBCFD),

    normalYellow = Color(0xFFFFE598),
    darkYellow = Color(0xFFA66F1C),

    backgroundGreen = Color(0xFF88D8B0),
    backgroundYellow = Color(0xFFFDCB7D),
    backgroundBlue = Color(0xFF83D1F5),

    invalidGrey = Color(0xFFB4B4B4)
)

val variantsColor = listOf(
    Color(0xFF88D8B0)
)

enum class StateColor(val color: Color) {
    Normal(paletteColor.normalBlue),
    Safe(paletteColor.normalGreen),
    Warning(paletteColor.normalYellow),
    Unsafe(paletteColor.normalRed)
}

