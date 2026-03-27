package com.example.linkgame.game.logic

import com.example.linkgame.game.model.Board

fun isBoardCleared(board: Board): Boolean {
    for (row in board.cells) for (v in row) if (v != 0) return false
    return true
}

fun isBoardSolvable(board: Board, maxAttempts: Int = 100): Boolean {
    repeat(maxAttempts) {
        val copy = Board(board.rows, board.cols, Array(board.rows) { board.cells[it].clone() })
        while (true) {
            val pairs = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
            for (r1 in 0 until copy.rows) {
                for (c1 in 0 until copy.cols) {
                    val v = copy.cells[r1][c1]
                    if (v == 0) continue
                    for (r2 in 0 until copy.rows) {
                        for (c2 in 0 until copy.cols) {
                            if (r1 == r2 && c1 == c2) continue
                            if (copy.cells[r2][c2] == v && canConnectWithPadding(copy, r1, c1, r2, c2)) {
                                pairs.add(Pair(Pair(r1, c1), Pair(r2, c2)))
                            }
                        }
                    }
                }
            }
            if (pairs.isEmpty()) break
            val (p1, p2) = pairs.random()
            copy.cells[p1.first][p1.second] = 0
            copy.cells[p2.first][p2.second] = 0
        }
        if (isBoardCleared(copy)) return true
    }
    return false
}