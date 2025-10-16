package com.example.smartbandiot

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartbandiot.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth // Diperlukan

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ... (Kode Fragment Management dan Bottom Navigation Anda)

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
            // Pastikan Anda telah mengimpor fragment-fragment ini: SettingsFragment, EditProfileFragment
            val shouldHideNavbar = currentFragment is SettingsFragment || currentFragment is EditProfileFragment

            // Pastikan navigationMain adalah ID yang benar untuk Bottom Navigation View Anda
            binding.navigationMain.visibility = if (shouldHideNavbar) View.GONE else View.VISIBLE
        }

        if (savedInstanceState == null) {
            // Pastikan Anda telah mengimpor fragment-fragment ini: HomeFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commit()
            // Asumsi bottomNavbarMainActivity memiliki ID home
            binding.bottomNavbarMainActivity.setItemSelected(R.id.home)
        }

        // ... (Listener Bottom Navigation Anda)

        binding.bottomNavbarMainActivity.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment())
                        .commit()
                }
                R.id.activity -> {
                    // Jika ActivityFragment berbeda dari HomeFragment, ganti di sini!
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment()) // <-- PERHATIAN: Masih HomeFragment
                        .commit()
                }
                R.id.history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, HistoryFragment())
                        .commit()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, UserProfileFragment())
                        .commit()
                }
            }
        }
    }

    // Memeriksa Status Autentikasi Saat Activity Dimulai
    override fun onStart() {
        super.onStart()

        // Jika pengguna tidak login (sesi hangus atau logout), kembalikan ke layar login
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java) // Kembali ke Activity Login
            startActivity(intent)
            finish() // Tutup MainActivity
        }
    }
}