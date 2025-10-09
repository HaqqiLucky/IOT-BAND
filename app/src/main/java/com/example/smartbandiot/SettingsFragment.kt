package com.example.smartbandiot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var btnBack: ImageButton
    private lateinit var layoutPersonalInfo: LinearLayout
    private lateinit var layoutChangePassword: LinearLayout
    private lateinit var layoutLinkedDevices: LinearLayout
    private lateinit var switchDarkMode: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Sembunyikan bottom navbar
        hideBottomNavBar()

        // Inisialisasi views
        initViews(view)

        // Setup click listeners
        setupClickListeners()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tampilkan kembali bottom navbar saat fragment dihancurkan
        showBottomNavBar()
    }

    private fun hideBottomNavBar() {
        // Sembunyikan container navbar (MaterialCardView)
        activity?.findViewById<View>(R.id.bottom_navbar_main_activity)?.visibility = View.GONE
    }

    private fun showBottomNavBar() {
        // Tampilkan container navbar (MaterialCardView)
        activity?.findViewById<View>(R.id.bottom_navbar_main_activity)?.visibility = View.VISIBLE
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        layoutPersonalInfo = view.findViewById(R.id.layoutPersonalInfo)
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword)
        layoutLinkedDevices = view.findViewById(R.id.layoutLinkedDevices)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
    }

    private fun setupClickListeners() {
        // Back button - kembali ke UserProfileFragment
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Personal Information - Navigasi ke UpdateSettingsFragment (Edit Profile)
        layoutPersonalInfo.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, EditProfileFragment())
                    .addToBackStack("settings")
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        // Change Password
        layoutChangePassword.setOnClickListener {
            Toast.makeText(context, "Membuka Change Password", Toast.LENGTH_SHORT).show()
            // TODO: Navigasi ke fragment Change Password
            // parentFragmentManager.beginTransaction()
            //     .replace(R.id.container, ChangePasswordFragment())
            //     .addToBackStack("settings")
            //     .commit()
        }

        // Linked Devices
        layoutLinkedDevices.setOnClickListener {
            Toast.makeText(context, "Membuka Linked Devices", Toast.LENGTH_SHORT).show()
            // TODO: Navigasi ke fragment Linked Devices
            // parentFragmentManager.beginTransaction()
            //     .replace(R.id.container, LinkedDevicesFragment())
            //     .addToBackStack("settings")
            //     .commit()
        }

        // Dark Mode Switch
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(context, "Dark Mode Aktif", Toast.LENGTH_SHORT).show()
                // TODO: Implementasi dark mode
                // Simpan preference dan ubah tema
            } else {
                Toast.makeText(context, "Dark Mode Nonaktif", Toast.LENGTH_SHORT).show()
                // TODO: Kembali ke light mode
            }
        }
    }
}