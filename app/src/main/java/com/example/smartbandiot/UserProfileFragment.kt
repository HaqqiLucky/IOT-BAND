package com.example.smartbandiot

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserProfileFragment : Fragment() {

    private lateinit var btnSettings: ImageButton
    private lateinit var imgProfile: ShapeableImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView

    private lateinit var tvWeight: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvAge: TextView

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)
        showBottomNavBar()

        btnSettings = view.findViewById(R.id.btnSettings)
        imgProfile = view.findViewById(R.id.imgProfile)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvWeight = view.findViewById(R.id.tvWeight)
        tvHeight = view.findViewById(R.id.tvHeight)
        tvAge = view.findViewById(R.id.tvAge)

        btnSettings.setOnClickListener {
            navigateToSettings()
        }

        if (currentUser != null) {
            databaseRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.uid)

            loadFirebaseData()
        }

        loadSharedPreferencesData()
        listenRealtimeHeartRate()

        return view
    }

    override fun onResume() {
        super.onResume()
        showBottomNavBar()
        if (currentUser != null && ::databaseRef.isInitialized) {
            loadSharedPreferencesData()
            loadFirebaseData()
        }
    }

    private fun loadFirebaseData() {
        if (!::databaseRef.isInitialized) return

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val firebaseWeight = snapshot.child("weight").getValue(String::class.java)
                    val firebaseHeight = snapshot.child("height").getValue(String::class.java)
                    val firebaseAge = snapshot.child("age").getValue(String::class.java)

                    if (!firebaseWeight.isNullOrBlank()) tvWeight.text = firebaseWeight
                    if (!firebaseHeight.isNullOrBlank()) tvHeight.text = firebaseHeight
                    if (!firebaseAge.isNullOrBlank()) tvAge.text = firebaseAge
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseProfile", "Failed to read Firebase value: ${error.message}")
            }
        })
    }

    private fun listenRealtimeHeartRate() {
        val hrRef = FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("data_iot").child("device_001").child("heart_rate")

        hrRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hr = snapshot.getValue(Int::class.java)
                if (hr != null) {
                    view?.findViewById<TextView>(R.id.tvHeartRate)?.text = hr.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadSharedPreferencesData() {
        val pref = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)

        if (currentUser != null) {
            tvUserName.text = currentUser.displayName ?: pref.getString("full_name", "Pengguna")
            tvUserEmail.text = currentUser.email ?: pref.getString("email", "Email Tidak Ditemukan")

            val photoUrl = currentUser.photoUrl
            if (photoUrl != null && isAdded) {
                Glide.with(this).load(photoUrl)
                    .placeholder(R.drawable.welcome)
                    .error(R.drawable.welcome)
                    .into(imgProfile)
            } else imgProfile.setImageResource(R.drawable.welcome)

        } else {
            tvUserName.text = pref.getString("full_name", "Tamu")
            tvUserEmail.text = pref.getString("email", "Silakan Login")
            imgProfile.setImageResource(R.drawable.welcome)
        }

        tvAge.text = pref.getString("age", "0")
        tvWeight.text = pref.getString("weight", "0.0")
        tvHeight.text = pref.getString("height", "0.0")
    }

    private fun showBottomNavBar() {
        activity?.findViewById<View>(R.id.navigation_main)?.visibility = View.VISIBLE
    }

    private fun navigateToSettings() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .addToBackStack(null)
            .commit()
    }
}
