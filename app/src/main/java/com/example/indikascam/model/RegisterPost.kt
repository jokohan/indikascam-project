package com.example.indikascam.model

data class RegisterPost(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)