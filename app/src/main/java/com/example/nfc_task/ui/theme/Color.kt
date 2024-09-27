package com.example.nfc_task.ui.theme

import androidx.compose.ui.graphics.Color

val purple1 = Color(0xFF8A2BE2)
val purple2 = Color(0xFFEE82EE)
val purple3 = Color(0xFFF3E8F6)


val lightGrey = Color(0xFFF3EFF3)
val mediumGrey = Color(0xFF939393)
val darkGrey = Color(0xFF666666)

val lightGreen = Color(0xFFB9EBD3)
val normalGreen = Color(0xFF4CA26C)
val darkGreen = Color(0xFF1A571A)

val normalRed = Color(0xFFFF6347)

val normalBlue = Color(0xFF00CED1)

val normalYellow = Color(0xFFFFB347)
val darkYellow = Color(0xFFA66F1C)

val warningColor = normalRed
val successColor = normalGreen

val backgroundGreen = Color(0xFF88D8B0)
val backgroundYellow = Color(0xFFFDCB7D)

enum class ThemeColor(val color: Color) {
    blue(normalBlue),
    yellow(normalYellow),
    red(normalRed),
    green(normalGreen)
}

enum class StateColor(val color: Color) {
    normal(normalBlue),
    safe(normalGreen),
    warning(normalYellow),
    unsafe(normalRed)
}

