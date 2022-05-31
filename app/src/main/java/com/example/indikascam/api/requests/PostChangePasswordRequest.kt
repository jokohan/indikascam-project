package com.example.indikascam.api.requests

data class PostChangePasswordRequest (
    val email: String,
    val new_password: String,
    val new_password_confirmation: String
    )