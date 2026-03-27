package com.example.linkgame.game.logic

import com.example.linkgame.game.model.Board
import com.example.linkgame.game.model.LevelConfig
import kotlin.random.Random

fun generateBoard(rows: Int, cols: Int, pairs: Int): Board {
    val total = rows * cols
    val values = (1..pairs).flatMap { listOf(it, it) }.shuffled()
    val cells = Array(rows) { IntArray(cols) }
    for (i in 0 until minOf(total, values.size)) {
        val r = i / cols
        val c = i % cols
        cells[r][c] = values[i]
    }
    return Board(rows, cols, cells)
}

fun generateSimpleSolvableBoard(rows: Int, cols: Int): Board {
    val total = rows * cols
    val pairs = total / 2
    val values = (1..pairs).flatMap { listOf(it, it) }.shuffled()
    val cells = Array(rows) { IntArray(cols) }
    for (i in 0 until minOf(total, values.size)) {
        val r = i / cols
        val c = i % cols
        cells[r][c] = values[i]
    }
    return Board(rows, cols, cells)
}

fun generateSolvableBoardForLevel(level: LevelConfig): Board {
    var b: Board
    var tries = 0
    val maxGenerationTries = 5000
    while (true) {
        b = generateBoard(level.rows, level.cols, level.pairs)
        if (isBoardSolvable(b, maxAttempts = 100)) return b
        tries++
        if (tries > maxGenerationTries) {
            return generateSimpleSolvableBoard(level.rows, level.cols)
        }
    }
}