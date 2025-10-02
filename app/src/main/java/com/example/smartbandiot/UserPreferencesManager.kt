package com.example.smartbandiot

import android.content.Context
import android.content.SharedPreferences
import com.example.smartbandiot.model.User

class UserPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "SmartBandUserPrefs"
        private const val KEY_NAME = "user_name"
        private const val KEY_WEIGHT = "user_weight"
        private const val KEY_HEIGHT = "user_height"
        private const val KEY_AGE = "user_age"
        private const val KEY_PROFILE_IMAGE = "user_profile_image"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_GENDER = "user_gender"
    }

    // Simpan data user
    fun saveUserData(user: User) {
        prefs.edit().apply {
            putString(KEY_NAME, user.name)
            putInt(KEY_WEIGHT, user.weight)
            putInt(KEY_HEIGHT, user.height)
            putInt(KEY_AGE, user.age)
            putString(KEY_PROFILE_IMAGE, user.profileImagePath)
            putString(KEY_EMAIL, user.email)
            putString(KEY_GENDER, user.gender)
            apply()
        }
    }

    // Ambil data user (dipanggil dari UserProfileActivity)
    fun getUserData(): User {
        return User(
            name = prefs.getString(KEY_NAME, "") ?: "",
            weight = prefs.getInt(KEY_WEIGHT, 0),
            height = prefs.getInt(KEY_HEIGHT, 0),
            age = prefs.getInt(KEY_AGE, 0),
            profileImagePath = prefs.getString(KEY_PROFILE_IMAGE, "") ?: "",
            email = prefs.getString(KEY_EMAIL, "") ?: "",
            gender = prefs.getString(KEY_GENDER, "") ?: ""
        )
    }

    // Clear semua data (opsional)
    fun clearUserData() {
        prefs.edit().clear().apply()
    }
}