package com.example.smartbandiot

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preferences)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.host_fragment_container_preferences) as NavHostFragment
        val navController = navHostFragment.navController


        if (savedInstanceState == null) {
//            val myFragment = ChooseGenderFragment()
//
//            supportFragmentManager.beginTransaction()
//                .add(R.id.host_fragment_container_preferences, myFragment)
//                .commit()

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.host_fragment_container_preferences).navigateUp() || super.onSupportNavigateUp()
    }
}