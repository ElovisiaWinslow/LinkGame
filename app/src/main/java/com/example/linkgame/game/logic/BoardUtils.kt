package com.example.linkgame.game.logic

import com.example.linkgame.game.model.Board

fun removeTiles(board: Board, r1: Int, c1: Int, r2: Int, c2: Int): Board {
    val newCells = Array(board.rows) { board.cells[it].clone() }
    newCells[r1][c1] = 0
    newCells[r2][c2] = 0
    return Board(board.rows, board.cols, newCells)
}