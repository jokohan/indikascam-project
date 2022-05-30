package com.example.indikascam.api.requests

data class PostRegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)