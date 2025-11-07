package com.example.smartbandiot

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private lateinit var btnBack: ImageButton
    private lateinit var layoutPersonalInfo: LinearLayout
    private lateinit var layoutLogout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        hideBottomNavBar()
        initViews(view)
        setupClickListeners()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavBar()
    }

    private fun hideBottomNavBar() {
        activity?.findViewById<View>(R.id.navigation_main)?.visibility = View.GONE
    }

    private fun showBottomNavBar() {
        activity?.findViewById<View>(R.id.navigation_main)?.visibility = View.VISIBLE
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        layoutPersonalInfo = view.findViewById(R.id.layoutPersonalInfo)
        layoutLogout = view.findViewById(R.id.layoutLogout)
    }

    private fun setupClickListeners() {

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        layoutPersonalInfo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, EditProfileFragment())
                .addToBackStack("settings")
                .commit()
        }

        layoutLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

}
