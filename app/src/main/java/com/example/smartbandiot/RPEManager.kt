package com.example.smartbandiot

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class RPEManager {

    private val database = FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.firebaseio.com/")
    private val ruleRef = database.getReference("rulebase")

    // Fungsi untuk update target Km berdasarkan RPE
    fun updateTarget(currentKm: Double, rpeValue: Int): Double {
        var newKm = currentKm
        when {
            rpeValue <= 3 -> newKm += currentKm * 0.015  // too easy
            rpeValue >= 8 -> newKm -= currentKm * 0.005  // too hard
        }
        Log.d("RPEManager", "RPE: $rpeValue, New Target Km: $newKm")
        saveToDatabase(newKm, rpeValue)
        return newKm
    }

    private fun saveToDatabase(kmCurrent: Double, rpeValue: Int) {
        val entry = mapOf(
            "kmCurrent" to kmCurrent,
            "rpeValue" to rpeValue,
            "timestamp" to System.currentTimeMillis()
        )
        ruleRef.push().setValue(entry)
    }
}
