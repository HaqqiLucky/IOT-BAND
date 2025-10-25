package com.example.smartbandiot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

        // 🔹 Tambahan: minta izin lokasi untuk fitur OSM (JoggingFragment)
        requestLocationPermission()

        // ... (Kode Fragment Management dan Bottom Navigation Anda)
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
            val shouldHideNavbar =
                currentFragment is SettingsFragment || currentFragment is EditProfileFragment

            binding.navigationMain.visibility = if (shouldHideNavbar) View.GONE else View.VISIBLE
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commit()
            binding.bottomNavbarMainActivity.setItemSelected(R.id.home)
        }

        binding.bottomNavbarMainActivity.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment())
                        .commit()
                }
                R.id.activity -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, JoggingFragment())
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

    // 🔹 Tambahan: Fungsi izin lokasi
    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    // Memeriksa Status Autentikasi Saat Activity Dimulai
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
