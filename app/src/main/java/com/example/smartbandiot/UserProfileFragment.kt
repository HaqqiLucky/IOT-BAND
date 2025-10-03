package com.example.smartbandiot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class UserProfileFragment : Fragment() {

    private lateinit var btnSettings: ImageButton   // ubah jadi ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke fragment_user_profile.xml
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        // Inisialisasi button settings
        btnSettings = view.findViewById(R.id.btnSettings)  // otomatis jadi ImageButton

        // click listener for button settings
        btnSettings.setOnClickListener {
            navigateToSettings()
        }

        return view
    }

    private fun navigateToSettings() {
        // Navigasi ke SettingsFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, SettingsFragment()) // container id sesuai sama di activity
            .addToBackStack(null)
            .commit()
    }
}
