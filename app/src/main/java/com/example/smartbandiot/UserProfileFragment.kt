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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

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

        // Inisialisasi UI
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

        // INISIALISASI FIREBASE DATABASE
        if (currentUser != null) {
            databaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)

            // PENTING: Panggil load data di sini JUGA untuk pemuatan awal
            loadFirebaseData()
        }

        // Muat data Shared Preferences (sebagai fallback dan untuk Age/Auth)
        loadSharedPreferencesData()

        return view
    }

    override fun onResume() {
        super.onResume()
        showBottomNavBar()
        // PENTING: Panggil load data di sini lagi untuk refresh data saat fragment kembali aktif
        if (currentUser != null && ::databaseRef.isInitialized) {
            // loadSharedPreferencesData() sudah dipanggil di onCreateView,
            // kita panggil lagi hanya untuk memastikan data terbaru (terutama Auth)
            loadSharedPreferencesData()
            loadFirebaseData()
        }
    }

    // --- FUNGSI MEMUAT DATA DARI FIREBASE DATABASE ---
    private fun loadFirebaseData() {
        if (!::databaseRef.isInitialized) {
            Log.e("FirebaseProfile", "databaseRef not initialized.")
            return
        }

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Mengambil nilai Weight/Height langsung di bawah UID
                    val firebaseWeight = snapshot.child("weight").getValue(String::class.java)
                    val firebaseHeight = snapshot.child("height").getValue(String::class.java)

                    Log.d("FirebaseProfile", "Loaded (Weight, Height): ($firebaseWeight, $firebaseHeight)")

                    // Update UI Weight (TANPA SATUAN!)
                    if (!firebaseWeight.isNullOrBlank() && firebaseWeight != "0.0" && firebaseWeight != "0") {
                        tvWeight.text = firebaseWeight
                    } else {
                        Log.w("FirebaseProfile", "Weight is null or zero. Keeping SharedPref/Default.")
                    }

                    // Update UI Height (TANPA SATUAN!)
                    if (!firebaseHeight.isNullOrBlank() && firebaseHeight != "0.0" && firebaseHeight != "0") {
                        tvHeight.text = firebaseHeight
                    } else {
                        Log.w("FirebaseProfile", "Height is null or zero. Keeping SharedPref/Default.")
                    }
                } else {
                    Log.w("FirebaseProfile", "User data snapshot not found in Firebase.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseProfile", "Failed to read Firebase value: ${error.message}")
            }
        })
    }
    // --- AKHIR FUNGSI FIREBASE ---

    // --- FUNGSI MEMUAT DATA DARI SHARED PREFERENCES (sebagai fallback) ---
    private fun loadSharedPreferencesData() {
        val sharedPref = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE)

        // Memuat data Auth
        if (currentUser != null) {
            tvUserName.text = currentUser.displayName ?: sharedPref.getString("full_name", "Pengguna")
            tvUserEmail.text = currentUser.email ?: sharedPref.getString("email", "Email Tidak Ditemukan")

            val photoUrl = currentUser.photoUrl
            if (photoUrl != null && isAdded) {
                Glide.with(this).load(photoUrl).placeholder(R.drawable.welcome).error(R.drawable.welcome).into(imgProfile)
            } else {
                imgProfile.setImageResource(R.drawable.welcome)
            }
        } else {
            tvUserName.text = sharedPref.getString("full_name", "Tamu")
            tvUserEmail.text = sharedPref.getString("email", "Silakan Login")
            imgProfile.setImageResource(R.drawable.welcome)
        }

        // Memuat Age, Weight, dan Height dari Shared Pref
        val age = sharedPref.getString("age", "0")
        tvAge.text = age ?: "0"

        // Menetapkan nilai dari Shared Pref, yang akan DITIMPA oleh Firebase jika berhasil dimuat
        tvWeight.text = sharedPref.getString("weight", "0.0")
        tvHeight.text = sharedPref.getString("height", "0.0")
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