package com.example.smartbandiot.model

import java.nio.file.Path

// Data class untuk menyimpan informasi user

data class User (
    val name: String,
    val weight: Int,
    val height: Int,
    val age: Int,
    val profileImagePath: String,
    val email: String,
    val gender: String
)
