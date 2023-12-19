package com.example.ch09_qrcodereader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ch09_qrcodereader.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvResult.text = intent.getStringExtra("msg")
        binding.btnGoBack.setOnClickListener { finish() }
    }
}