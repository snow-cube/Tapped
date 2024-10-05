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
    normalGreen = Color(0xFF4CA26C),
    darkGreen = Color(0xFF1A571A),

    normalRed = Color(0xFFFF6347),

    normalBlue = Color(0xFF00CED1),

    normalYellow = Color(0xFFFFB347),
    darkYellow = Color(0xFFA66F1C),

    backgroundGreen = Color(0xFF88D8B0),
    backgroundYellow = Color(0xFFFDCB7D),
    backgroundBlue = Color(0xFF00CED1),

    invalidGrey = Color(0xFFB4B4B4)
)

val variantsColor = listOf(
    paletteColor.normalBlue,
    paletteColor.normalYellow,
    paletteColor.normalRed,
    paletteColor.normalGreen
)

enum class StateColor(val color: Color) {
    Normal(paletteColor.normalBlue),
    Safe(paletteColor.normalGreen),
    Warning(paletteColor.normalYellow),
    Unsafe(paletteColor.normalRed)
}

