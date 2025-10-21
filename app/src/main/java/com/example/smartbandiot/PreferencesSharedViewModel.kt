package com.example.smartbandiot

import androidx.lifecycle.ViewModel

class PreferencesSharedViewModel : ViewModel() {
    var weight: Double = 0.0
    var height: Double = 0.0
    var gender: String = ""
    var birthYYYYmm: String = ""
    var goal: String = ""
}