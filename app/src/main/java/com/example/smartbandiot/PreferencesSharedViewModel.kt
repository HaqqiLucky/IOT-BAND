package com.example.smartbandiot

import androidx.lifecycle.ViewModel

class PreferencesSharedViewModel : ViewModel() {
    var weight: Double = 0.0
    var height: Double = 0.0
    var gender: String = ""
    var age: Int = 0
    var goal: String = ""
}