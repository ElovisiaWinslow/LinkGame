package com.example.linkgame.game.logic

import com.example.linkgame.game.model.Board

/**
 * 查找两点之间的连接路径（最多两个拐角）
 * @return 路径坐标列表（包括起点和终点），若无法连接则返回 null
 */
fun findConnectPath(board: Board, r1: Int, c1: Int, r2: Int, c2: Int): List<Pair<Int, Int>>? {
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

    if (sr == tr && sc == tc) return null
    val tileValue = board.cells[r1][c1]
    if (tileValue == 0) return null
    if (board.cells[r2][c2] != tileValue) return null

    val dr = intArrayOf(-1, 1, 0, 0)
    val dc = intArrayOf(0, 0, -1, 1)
    val visited = Array(R + 2) { Array(C + 2) { IntArray(4) { Int.MAX_VALUE } } }
    val parent = mutableMapOf<Triple<Int, Int, Int>, Triple<Int, Int, Int>>()

    data class State(val r: Int, val c: Int, val dir: Int, val turns: Int)
    val q = java.util.ArrayDeque<State>()

    for (d in 0..3) {
        val nr = sr + dr[d]
        val nc = sc + dc[d]
        if (nr in 0..R + 1 && nc in 0..C + 1) {
            val canEnter = (nr == tr && nc == tc) || (P[nr][nc] == 0)
            if (canEnter) {
                visited[nr][nc][d] = 0
                parent[Triple(nr, nc, d)] = Triple(sr, sc, d)
                q.add(State(nr, nc, d, 0))
            }
        }
    }

    while (q.isNotEmpty()) {
        val (r, c, dir, turns) = q.removeFirst()
        if (r == tr && c == tc) {
            val path = mutableListOf<Pair<Int, Int>>()
            var cr = r
            var cc = c
            var cdir = dir
            while (true) {
                path.add(Pair(cr - 1, cc - 1))
                val prev = parent[Triple(cr, cc, cdir)] ?: break
                val (pr, pc, pdir) = prev
                if (pr == sr && pc == sc) {
                    path.add(Pair(sr - 1, sc - 1))
                    break
                }
                cr = pr
                cc = pc
                cdir = pdir
            }
            path.reverse()
            return path.takeIf { it.isNotEmpty() }
        }

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
                parent[Triple(nr, nc, nd)] = Triple(r, c, dir)
                q.add(State(nr, nc, nd, nextTurns))
            }
        }
    }
    return null
}