package com.example.smartbandiot.model

// Contoh di SetupActivity atau OnboardingActivity
val userPrefs = UserPreferencesManager(this)

val user = User(
    name = etName.text.toString(),
    weight = etWeight.text.toString().toInt(),
    height = etHeight.text.toString().toInt(),
    age = etAge.text.toString().toInt(),
    profileImagePath = selectedImageUri.toString(),
    email = etEmail.text.toString(),
    gender = selectedGender
)

userPrefs.saveUserData(user)

data class User()
