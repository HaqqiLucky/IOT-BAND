package com.example.smartbandiot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class UserProfileFragment : Fragment() {

    private lateinit var btnSettings: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        // Tampilkan navbar saat di UserProfile
        showBottomNavBar()

        // Inisialisasi button settings
        btnSettings = view.findViewById(R.id.btnSettings)

        // click listener for button settings
        btnSettings.setOnClickListener {
            navigateToSettings()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // UnHide navbar
        showBottomNavBar()
    }

    private fun showBottomNavBar() {
//        activity?.findViewById<View>(R.id.bottom_navbar_main_activity)?.visibility = View.VISIBLE
        activity?.findViewById<View>(R.id.bottom_navbar_main_activity)?.visibility = View.VISIBLE


    }

    private fun navigateToSettings() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .addToBackStack(null)
            .commit()
    }
}