package com.example.linkgame.utils

import androidx.compose.ui.graphics.Color

fun colorForValue(v: Int): Color {
    val palette = listOf(
        Color(0xFFE57373), Color(0xFFF06292), Color(0xFFBA68C8), Color(0xFF9575CD),
        Color(0xFF64B5F6), Color(0xFF4FC3F7), Color(0xFF4DD0E1), Color(0xFF4DB6AC),
        Color(0xFF81C784), Color(0xFFDCE775), Color(0xFFFFD54F), Color(0xFFFF8A65),
        Color(0xFF90A4AE), Color(0xFF26A69A), Color(0xFF29B6F6), Color(0xFF42A5F5)
    )
    val idx = (v - 1) % palette.size
    return palette[idx]
}