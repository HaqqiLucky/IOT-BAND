package com.example.smartbandiot.model

import com.google.firebase.auth.FirebaseAuth
import java.nio.file.Path

val auth = FirebaseAuth.getInstance()
val firebaseUser = auth.currentUser
data class User (
    val uid: String = firebaseUser?.uid ?: "",
    val name: String = firebaseUser?.displayName ?: "",
    val weight: Double = 0.0, //
    val height: Double = 0.0, //
    var birthYYYYmm: String = "", //
    val profileImagePath: String = "",
    val email: String = "",
    val gender: String = "", //
    val goal: String = "" //
)
// hy leni dan jawa kalo klean baca ini aku mau kasi tau kalo beberapa variabel ini krusial karena di pake buat rule based,yg aku komen beraarri sangat berharga