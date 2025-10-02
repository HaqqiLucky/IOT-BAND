package com.example.smartbandiot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
//import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView

class UserProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var imgProfile: ShapeableImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvAge: TextView
    private lateinit var btnToday: Button
    private lateinit var btnWeek: Button
    private lateinit var btnMonth: Button
    private lateinit var tvHeartRate: TextView
    private lateinit var tvSleepHours: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var userPrefs: UserPreferencesManager
    private var selectedTab = "today"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userPrefs = UserPreferencesManager(this)

        initViews()
        setupListeners()
        loadUserData()  // Load data dari input user
        loadHealthData("today")
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSettings = findViewById(R.id.btnSettings)
        imgProfile = findViewById(R.id.imgProfile)
        tvUserName = findViewById(R.id.tvUserName)
        tvWeight = findViewById(R.id.tvWeight)
        tvHeight = findViewById(R.id.tvHeight)
        tvAge = findViewById(R.id.tvAge)
        btnToday = findViewById(R.id.btnToday)
        btnWeek = findViewById(R.id.btnWeek)
        btnMonth = findViewById(R.id.btnMonth)
        tvHeartRate = findViewById(R.id.tvHeartRate)
        tvSleepHours = findViewById(R.id.tvSleepHours)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        btnToday.setOnClickListener {
            selectTab("today")
            loadHealthData("today")
        }

        btnWeek.setOnClickListener {
            selectTab("week")
            loadHealthData("week")
        }

        btnMonth.setOnClickListener {
            selectTab("month")
            loadHealthData("month")
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigate to home
                    true
                }
                R.id.nav_activity -> {
                    // Navigate to activity
                    true
                }
                R.id.nav_heart -> {
                    // Navigate to heart monitor
                    true
                }
                R.id.nav_profile -> {
                    // Already on profile
                    true
                }
                else -> false
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_profile
    }

    private fun selectTab(tab: String) {
        selectedTab = tab

        btnToday.setBackgroundResource(R.drawable.tab_button_unselected)
        btnWeek.setBackgroundResource(R.drawable.tab_button_unselected)
        btnMonth.setBackgroundResource(R.drawable.tab_button_unselected)

        val grayColor = ContextCompat.getColor(this, android.R.color.darker_gray)
        btnToday.setTextColor(grayColor)
        btnWeek.setTextColor(grayColor)
        btnMonth.setTextColor(grayColor)

        val whiteColor = ContextCompat.getColor(this, android.R.color.white)
        when (tab) {
            "today" -> {
                btnToday.setBackgroundResource(R.drawable.tab_button_selected)
                btnToday.setTextColor(whiteColor)
            }
            "week" -> {
                btnWeek.setBackgroundResource(R.drawable.tab_button_selected)
                btnWeek.setTextColor(whiteColor)
            }
            "month" -> {
                btnMonth.setBackgroundResource(R.drawable.tab_button_selected)
                btnMonth.setTextColor(whiteColor)
            }
        }
    }

    // LOAD DATA DARI INPUT USER
    private fun loadUserData() {
        val user = userPrefs.getUserData()

        // Set data sesuai input user
        tvUserName.text = user.name
        tvWeight.text = user.weight.toString()
        tvHeight.text = user.height.toString()
        tvAge.text = user.age.toString()

        // Load profile image jika ada
        if (user.profileImagePath.isNotEmpty()) {
            Glide.with(this)
                .load(user.profileImagePath)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .circleCrop()
                .into(imgProfile)
        } else {
            imgProfile.setImageResource(R.drawable.default_profile)
        }
    }

    // Load health data (heart rate, sleep) dari smart band
    private fun loadHealthData(period: String) {
        // TODO: Nanti connect ke smart band via Bluetooth
        // Untuk sekarang gunakan data dummy
        when (period) {
            "today" -> {
                tvHeartRate.text = "115"
                tvSleepHours.text = "8:50"
            }
            "week" -> {
                tvHeartRate.text = "112"
                tvSleepHours.text = "8:15"
            }
            "month" -> {
                tvHeartRate.text = "110"
                tvSleepHours.text = "8:30"
            }
        }
    }
}