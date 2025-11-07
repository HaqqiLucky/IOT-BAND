package com.example.smartbandiot

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class RPEManager(val uid:String) {

    private val database = FirebaseDatabase.getInstance("https://smartbandforteens-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val ruleRef = database.getReference("rulebase")
    private val userChallengeRef = database.getReference("users").child(uid).child("today_challenge")

    fun convertStringToRPE(rpeText:String): Int {
        return when(rpeText){
            "Very Tired" -> 9
            "Tired" -> 7
            "Normal" -> 5
            "Easy" -> 3
            "Very Easy" -> 1
            else -> 5
        }
    }

    fun updateTarget(currentKm: Double, rpeText:String): Double {
        val rpeValue = convertStringToRPE(rpeText)
        var newKm = currentKm

        when {
            rpeValue <= 3 -> newKm += currentKm * 0.015
            rpeValue >= 8 -> newKm -= currentKm * 0.005
        }

        saveToDatabase(newKm, rpeValue)
        userChallengeRef.child("target_km_next").setValue(newKm)
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
