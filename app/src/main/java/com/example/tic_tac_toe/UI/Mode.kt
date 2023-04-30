package com.example.tic_tac_toe.UI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tic_tac_toe.databinding.ActivityModeBinding

class Mode : AppCompatActivity() {
    private lateinit var binding: ActivityModeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.offline.setOnClickListener {
            val intent= Intent(this, Home::class.java)
            intent.putExtra("player","Multiplayer")
            startActivity(intent)
        }
        binding.online.setOnClickListener {
            startActivity(Intent(this, Join::class.java))
        }
    }
}