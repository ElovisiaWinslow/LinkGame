package com.example.linkgame.utils

fun labelForValue(v: Int): String {
    val ch = 'A' + ((v - 1) % 26)
    return ch.toString()
}