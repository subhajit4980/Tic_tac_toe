package com.example.tic_tac_toe.UI

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tic_tac_toe.Board.Board
import com.example.tic_tac_toe.Cell
import com.example.tic_tac_toe.R
import com.example.tic_tac_toe.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {
    private val boardCells = Array(3) { arrayOfNulls<ImageView>(3) }
    var board = Board()
    private var playerOneTurn = true
    var player=""
    private var currentPlayer = ""
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        player= intent.getStringExtra("player").toString()
        loadBoard()
        binding.buttonRestart.setOnClickListener {
            board = Board()
            currentPlayer = Board.PLAYER1
            binding.result.text = ""
            mapBoardToUi()
        }
        updateTurnText()
    }
    @SuppressLint("SetTextI18n")
    private fun updateTurnText() {
        binding.result.text = "Player ${if (currentPlayer == Board.PLAYER1) "X" else "O"}'s turn"
    }
    private fun mapBoardToUi() {
        for (i in board.board.indices) {
            for (j in board.board.indices) {
                when (board.board[i][j]) {
                    Board.PLAYER1 -> {
                        boardCells[i][j]?.setImageResource(R.drawable.circle)
                        boardCells[i][j]?.isEnabled = false
                    }
                    Board.PLAYER2 -> {
                        boardCells[i][j]?.setImageResource(R.drawable.cross)
                        boardCells[i][j]?.isEnabled = false
                    }
                    else -> {
                        boardCells[i][j]?.setImageResource(0)
                        boardCells[i][j]?.isEnabled = true
                    }
                }
            }
        }
    }

    private fun loadBoard() {

        for (i in boardCells.indices) {
            for (j in boardCells.indices) {
                boardCells[i][j] = ImageView(this)
                boardCells[i][j]?.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                    width = 250
                    height = 230
                    bottomMargin = 5
                    topMargin = 5
                    leftMargin = 5
                    rightMargin = 5
                }
                boardCells[i][j]?.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimary
                    )
                )
                binding.layoutBoard.addView(boardCells[i][j])
                boardCells[i][j]?.setOnClickListener(CellClickListener(i, j))
            }
        }
    }
    inner class CellClickListener(
        val i: Int,
        val j: Int
    ) : View.OnClickListener {

        override fun onClick(p0: View?) {
            if (!board.isGameOver) {
                val cell = Cell(i, j)
                if(player=="Multiplayer"){
                    currentPlayer = if (playerOneTurn) Board.PLAYER1 else Board.PLAYER2
                    board.placeMove(cell, currentPlayer)
                    playerOneTurn = !playerOneTurn
                    updateTurnText()
                    mapBoardToUi()
                }else{
                    board.placeMove(cell, Board.PLAYER1)
                    board.minimax(0, Board.PLAYER2)
                    board.computersMove?.let {
                        board.placeMove(it, Board.PLAYER2)
                    }
                    mapBoardToUi()
                }
            }
            when {
                board.hasPlayer2Won() -> binding.result.text = "Player X  Won "
                board.hasPlayerWon() -> {
                    binding.result.text = "Player O Won"
                    binding.congo.apply {
                        visibility= View.VISIBLE
                        playAnimation()
                        Handler(Looper.getMainLooper()).postDelayed({
                            cancelAnimation()
                            visibility= View.GONE
                        },7000)
                    }
                }
                board.isGameOver -> binding.result.text = "Game Tied "
            }

        }

    }

}