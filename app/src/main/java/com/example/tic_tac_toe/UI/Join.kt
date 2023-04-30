package com.example.tic_tac_toe.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tic_tac_toe.UI.Online_Home
import com.example.tic_tac_toe.databinding.ActivityJoinBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Join : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: ActivityJoinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this);
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        remove()
        binding.create.setOnClickListener {
            binding.contv.visibility= View.GONE
            binding.progressBar.visibility= View.VISIBLE
            createcode()
        }
        binding.join.setOnClickListener {
            val code = binding.GameCode.text
            if (code != null && code.toString() != "") {
                binding.contv.visibility= View.GONE
                binding.progressBar.visibility= View.VISIBLE
                available(binding.GameCode.text.toString())
            } else {
                Toast.makeText(this, "Game code Required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createcode() {
        val ref = db.collection("Code").document()
        val model = hashMapOf(
            "Code" to ref.id.toString(),
            "Player1" to 1,
        )
        ref.set(model).addOnSuccessListener {
            val intent = Intent(this, Online_Home::class.java)
            intent.putExtra("code", ref.id.toString())
            intent.putExtra("player", "PLAYER1")
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun available(s: String): Boolean {
        val collectionRef = db.collection("Code")
        val documentRef = collectionRef.document(s)
        var flag = false
        documentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                flag = documentSnapshot.exists()
                if (documentSnapshot.exists() && !documentSnapshot.contains("Player2")) {
                    val upd = hashMapOf(
                        "Player2" to "1",
                        "currentplayer" to "PLAYER1"
                    )
                    documentRef.update(upd as Map<String, Any>).addOnSuccessListener {
                        val intent = Intent(this, Online_Home::class.java)
                        intent.putExtra("code", s)
                        intent.putExtra("player", "PLAYER2")
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Room is Full or Code is Invalid", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error checking document existence.", e)
            }
        return flag
    }

    fun remove() {
        val code = intent.getStringExtra("code").toString()
        val collectionRef = db.collection("Code").document(code)
        collectionRef.addSnapshotListener { value, error ->
            if (value!!.exists() && value.contains("PLAYER1exit") && value.contains("PLAYER2exit")||value.exists() && !value.contains("Player2")) {
                collectionRef.delete()
            }
        }
    }
}