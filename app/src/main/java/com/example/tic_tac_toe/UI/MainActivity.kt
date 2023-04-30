package com.example.tic_tac_toe.UI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tic_tac_toe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.single.setOnClickListener {
                startActivity(Intent(this, Home::class.java))
            }
        binding.multi.setOnClickListener {
                startActivity(Intent(this, Mode::class.java))
            }
    }
}