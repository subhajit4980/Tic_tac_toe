package com.example.tic_tac_toe.Board

import com.example.tic_tac_toe.Cell

class Board {
    val board = Array(3) { arrayOfNulls<String>(3) }
    private val availableCells: List<Cell>
        get() {
            val cells = mutableListOf<Cell>()
            for (i in board.indices) {
                for (j in board.indices) {
                    if (board[i][j].isNullOrEmpty()) {
                        cells.add(Cell(i, j))
                    }
                }
            }
            return cells
        }
    val isGameOver: Boolean
        get() = hasPlayer2Won() || hasPlayerWon() || availableCells.isEmpty()
    fun hasPlayer2Won(): Boolean {
        if (board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == PLAYER2 ||
            board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == PLAYER2
        ) {
            return true
        }
        for (i in board.indices) {
            if (
                board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == PLAYER2 ||
                board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == PLAYER2
            ) {
                return true
            }
        }

        return false
    }

    fun hasPlayerWon(): Boolean {

        if (board[0][0] == board[1][1] && board[0][0] == board[2][2] && board[0][0] == PLAYER1 ||
            board[0][2] == board[1][1] && board[0][2] == board[2][0] && board[0][2] == PLAYER1
        ) {
            return true
        }

        for (i in board.indices) {
            if (
                board[i][0] == board[i][1] && board[i][0] == board[i][2] && board[i][0] == PLAYER1 ||
                board[0][i] == board[1][i] && board[0][i] == board[2][i] && board[0][i] == PLAYER1
            ) {
                return true
            }
        }

        return false
    }

    var computersMove: Cell? = null
    fun minimax(depth: Int, player: String): Int {
        if (hasPlayer2Won()) return +1
        if (hasPlayerWon()) return -1
        if (availableCells.isEmpty()) return 0

        var min = Integer.MAX_VALUE
        var max = Integer.MIN_VALUE

        for (i in availableCells.indices) {
            val cell = availableCells[i]
            if (player == PLAYER2) {
                placeMove(cell, PLAYER2)
                val currentScore = minimax(depth + 1, PLAYER1)
                max = Math.max(currentScore, max)

                if (currentScore >= 0) {
                    if (depth == 0) computersMove = cell
                }

                if (currentScore == 1) {
                    board[cell.i][cell.j] = ""
                    break
                }

                if (i == availableCells.size - 1 && max < 0) {
                    if (depth == 0) computersMove = cell
                }

            } else if (player == PLAYER1) {
                placeMove(cell, PLAYER1)
                val currentScore = minimax(depth + 1, PLAYER2)
                min = Math.min(currentScore, min)

                if (min == -1) {
                    board[cell.i][cell.j] = ""
                    break
                }
            }
            board[cell.i][cell.j] = ""
        }

        return if (player == PLAYER2) max else min
    }
    fun placeMove(cell: Cell, player: String) {
        board[cell.i][cell.j] = player
    }
    companion object {
        const val PLAYER1 = "O"
        const val PLAYER2 = "X"
    }

}