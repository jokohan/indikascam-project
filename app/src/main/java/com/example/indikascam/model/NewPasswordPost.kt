package com.example.indikascam.model

data class NewPasswordPost(
    val email: String,
    val new_password: String,
    val new_password_confirmation: String
)