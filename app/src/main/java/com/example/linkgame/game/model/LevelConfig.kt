package com.example.linkgame.game.model

data class LevelConfig(val name: String, val rows: Int, val cols: Int, val pairs: Int, val timeLimit: Int)

val ALL_LEVELS = listOf(
    LevelConfig("简单", 4, 4, 8, 60),
    LevelConfig("普通", 6, 4, 12, 80),
    LevelConfig("困难", 6, 5, 15, 100),
    LevelConfig("极限", 8, 5, 20, 110)
)