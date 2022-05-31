package com.example.indikascam.api.requests

data class PostChangePasswordVerificationRequest (
    val email: String,
    val token: String
    )