package com.example.linkgame.game.model

sealed class GameMode {
    object Challenge : GameMode()
    data class Endless(val difficulty: LevelConfig) : GameMode()
}