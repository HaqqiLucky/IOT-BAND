package com.example.smartbandiot.model

import com.google.firebase.auth.FirebaseAuth
import java.nio.file.Path

// Data class untuk menyimpan informasi user
val auth = FirebaseAuth.getInstance()
val firebaseUser = auth.currentUser
data class User (
    val uid: String = firebaseUser?.uid ?: "",
    val name: String = firebaseUser?.displayName ?: "",
    val weight: Double, //
    val height: Double, //
    val age: Int, //
    val profileImagePath: String,
    val email: String,
    val gender: String, //
    val goal: String //
)
// hy leni dan jawa kalo klean baca ini aku mau kasi tau kalo beberapa variabel ini krusial karena di pake buat rule based,yg aku komen beraarri sangat berharga