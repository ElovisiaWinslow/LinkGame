package com.example.linkgame.game.logic

import com.example.linkgame.game.model.Board

/**
 * 检测两个位置是否可以通过最多两次拐角连接（带边界填充）
 */
fun canConnectWithPadding(board: Board, r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
    val R = board.rows
    val C = board.cols
    val P = Array(R + 2) { IntArray(C + 2) { 0 } }
    for (i in 0 until R) {
        for (j in 0 until C) {
            P[i + 1][j + 1] = board.cells[i][j]
        }
    }
    val sr = r1 + 1
    val sc = c1 + 1
    val tr = r2 + 1
    val tc = c2 + 1
    val dr = intArrayOf(-1, 1, 0, 0)
    val dc = intArrayOf(0, 0, -1, 1)
    val visited = Array(R + 2) { Array(C + 2) { IntArray(4) { Int.MAX_VALUE } } }
    data class Node(val r: Int, val c: Int, val dir: Int, val turns: Int)
    val q = java.util.ArrayDeque<Node>()

    for (d in 0..3) {
        val nr = sr + dr[d]
        val nc = sc + dc[d]
        if (nr in 0..R + 1 && nc in 0..C + 1) {
            val ok = (nr == tr && nc == tc) || (P[nr][nc] == 0)
            if (ok) {
                q.add(Node(nr, nc, d, 0))
                visited[nr][nc][d] = 0
            }
        }
    }

    while (q.isNotEmpty()) {
        val cur = q.removeFirst()
        val r = cur.r
        val c = cur.c
        val dir = cur.dir
        val turns = cur.turns
        if (r == tr && c == tc) return true
        for (nd in 0..3) {
            val nr = r + dr[nd]
            val nc = c + dc[nd]
            if (nr !in 0..R + 1 || nc !in 0..C + 1) continue
            val nextTurns = turns + if (nd == dir) 0 else 1
            if (nextTurns > 2) continue
            val canEnter = (nr == tr && nc == tc) || (P[nr][nc] == 0)
            if (!canEnter) continue
            if (visited[nr][nc][nd] > nextTurns) {
                visited[nr][nc][nd] = nextTurns
                q.add(Node(nr, nc, nd, nextTurns))
            }
            if (nr == tr && nc == tc) return true
        }
    }
    return false
}