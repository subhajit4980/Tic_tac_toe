package com.example.tic_tac_toe.Board

import android.content.ContentValues.TAG
import android.util.Log
import com.example.tic_tac_toe.Cell
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OnlineBoard(val gameCode: String) {
    val boardRef =
        Firebase.firestore.collection("Code").document(gameCode)
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
    fun update() {
//        boardRef.collection("reset").document("reset").update("reset",1)
        // Upload the board array to Firestore
        val boardData = mutableMapOf<String, Any>()
        for (i in board.indices) {
            for (j in board[i].indices) {
                val cellValue = board[i][j] ?: ""
                boardData["${i}_${j}"] = cellValue
            }
        }
        boardRef.collection("board").document("boardData").set(boardData)
    }

   suspend fun fetch() {
        val boardRef =
            // Fetch the board array from Firestore
        boardRef.collection("board").document("boardData").addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val boardData = snapshot.data
                for (key in boardData!!.keys) {
                    val parts = key.split("_")
                    val i = parts[0].toInt()
                    val j = parts[1].toInt()
                    board[i][j] = boardData[key] as String?
                }
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
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
  suspend  fun placeMove(cell: Cell, player: String) {
        board[cell.i][cell.j] = player
        update()
    }

    companion object {
        const val PLAYER1 = "O"
        const val PLAYER2 = "X"
    }

}