package com.example.tic_tac_toe.UI

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tic_tac_toe.Board.Board
import com.example.tic_tac_toe.Board.OnlineBoard
import com.example.tic_tac_toe.Cell
import com.example.tic_tac_toe.R
import com.example.tic_tac_toe.databinding.ActivityOnlineHomeBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Online_Home : AppCompatActivity() {
    private lateinit var binding: ActivityOnlineHomeBinding
    private var backPressedTime = 0L
    private val db = Firebase.firestore
    private val boardCells = Array(3) { arrayOfNulls<ImageView>(3) }
    public var board: OnlineBoard? = null
    public var playerTurn = ""
    public var player = ""
    var gameCode = ""
    public var currentPlayer = ""
    private lateinit var job: Job
    public lateinit var ref: DocumentReference
    var show = false
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlineHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gameCode = intent.getStringExtra("code").toString()
        player = intent.getStringExtra("player").toString()
        board = OnlineBoard(gameCode)
        ref = db.collection("Code").document(gameCode)
        check(gameCode)
        loadBoard()
        updateTurnText()
        currentplayer(gameCode)
        updateUi()
        binding.buttonRestart.setOnClickListener {
            reset("do")
        }
    }

    public fun currentplayer(gameCode: String) {
        ref.addSnapshotListener { value, error ->
            if (value!!.exists()) {
                playerTurn = value.get("currentplayer").toString()
                updateTurnText()
            }
            if (value!!.exists() && value.contains("reset")) {
                reset("reflect")
                ref.update("reset", FieldValue.delete())
            }
        }
    }

    suspend fun update_currentplayer(player: String) {
        ref.update("currentplayer", player)

    }

    private fun updateUi() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                mapBoardToUi()
                showwin()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    private fun check(gameCode: String) {
        present(gameCode) { ifpresent ->
            if (ifpresent) {
                binding.invite.visibility = View.GONE
                binding.content.visibility = View.VISIBLE

            } else {
                binding.invite.visibility = View.VISIBLE
                binding.content.visibility = View.GONE
                invitefun(gameCode)
            }
        }
    }

    private fun invitefun(gameCode: String) {
        binding.code.text = gameCode
        binding.code.setOnClickListener {
            Toast.makeText(this, "Long tap to copy code", Toast.LENGTH_SHORT).show()
        }
        binding.code.setOnLongClickListener {
            val clipboard: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", binding.code.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this@Online_Home, "Text copied to clipboard", Toast.LENGTH_SHORT)
                .show()
            true
        }
        binding.send.setOnClickListener {
            val message = "Hey! join the game with the code : ${gameCode}"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(shareIntent, "Invite Player"))

        }
        var stop = false
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                present(gameCode) { ifpresent ->
                    if (ifpresent) {
                        binding.invite.visibility = View.GONE
                        binding.content.visibility = View.VISIBLE
                        stop = true
                    }
                }
                handler.postDelayed(this, 2000)
            }
        }
        if (stop) handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 1000)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
            ref.update(player + "exit", 1)
            val intent = Intent(this, Join::class.java)
            intent.putExtra("code", gameCode)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Press back again to exit the room", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun present(s: String, callback: (Boolean) -> Unit) {
        val collectionRef = db.collection("Code")
        val documentRef = collectionRef.document(s)
        documentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists() && documentSnapshot.contains("Player2")) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error checking document existence.", e)
                callback(false)
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
            if (!board!!.isGameOver) {
                val cell = Cell(i, j)
                currentplayer(gameCode)
                if (player == playerTurn) {
                    if (player == "PLAYER1") {
                        currentPlayer = Board.PLAYER1
                        runBlocking {
                            launch { board!!.placeMove(cell, currentPlayer) }
                            launch { mapBoardToUi() }
                            launch { update_currentplayer("PLAYER2") }
                            launch { updateTurnText() }
                        }

                    }
                    if (player == "PLAYER2") {
                        currentPlayer = Board.PLAYER2
                        runBlocking {
                            launch { board!!.placeMove(cell, currentPlayer) }
                            launch { mapBoardToUi() }
                            launch { update_currentplayer("PLAYER1") }
                            launch { updateTurnText() }
                        }
                    }
                } else {
                    Toast.makeText(this@Online_Home, "This is Not your turn", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            when {
                board!!.hasPlayer2Won() -> {
                    uploadwin("Player X  Won ")
                    Handler().postDelayed({
                        showcongo()
                    }, 1500)
                }
                board!!.hasPlayerWon() -> {
                    uploadwin("Player O  Won ")
                    Handler().postDelayed({
                        showcongo()
                    }, 1000)
                }
                board!!.isGameOver -> uploadwin("Game Tied ")
            }

        }
    }

    private fun uploadwin(msg: String) {
        ref.update("winner", msg)
    }

    private fun showwin() {
        ref.addSnapshotListener { value, error ->
            if (value!!.exists() && value.contains("winner")) {
                val msg = value.get("winner").toString()
                binding.result.text = msg
                binding.buttonRestart.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTurnText() {
        if (playerTurn == "PLAYER2") {
            binding.result.text = "Player X's turn"
        } else {
            binding.result.text = "Player O's turn"
        }
    }

    private fun mapBoardToUi() {
        runBlocking {
            launch {
                board!!.fetch()
            }
            launch {

                for (i in board!!.board.indices) {
                    for (j in board!!.board.indices) {
                        when (board!!.board[i][j]) {
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
        }


    }

    fun reset(p: String) {
        val updates = HashMap<String, Any>()
        updates["winner"] = FieldValue.delete()
        updates["currentplayer"] = "PLAYER1"
        if (p == "do") {
            updates["reset"] = "1"
        }
        ref.update(updates)
        board = OnlineBoard(gameCode)
        board!!.update()
        mapBoardToUi()
        show = false
        binding.buttonRestart.visibility = View.GONE
    }

    fun showcongo() {
        if (player != playerTurn && !show) {
            binding.congo.apply {
                scaleType = ImageView.ScaleType.FIT_XY
                setAnimation(R.raw.congo)
                visibility = View.VISIBLE
                playAnimation()
                Handler(Looper.getMainLooper()).postDelayed({
                    cancelAnimation()
                    visibility = View.GONE
                }, 10000)
            }
            show = true
        }
    }
}