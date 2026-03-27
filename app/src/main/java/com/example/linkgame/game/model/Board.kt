package com.example.linkgame.game.model

data class Board(val rows: Int, val cols: Int, val cells: Array<IntArray>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Board
        if (rows != other.rows) return false
        if (cols != other.cols) return false
        return cells.contentDeepEquals(other.cells)
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + cells.contentDeepHashCode()
        return result
    }
}