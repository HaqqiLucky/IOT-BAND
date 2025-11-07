package com.example.smartbandiot

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartbandiot.databinding.ActivityRpeFinishingRequestBinding

class RpeFinishingRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRpeFinishingRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRpeFinishingRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.circularProgressBar.apply {
            setProgressWithAnimation(100f, 3000)
        }

        binding.continu.isEnabled = false
        binding.circularProgressBar.onProgressChangeListener = { progress ->
            val percent = progress.toInt()
            binding.persenanloading.text = "$percent%"
            binding.continu.isEnabled = (percent == 100)
        }

        binding.continu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
