package com.example.indikascam.api.requests

data class PostBlockingCallRequest (
    val phone_number: String,
    val is_blocked: Int,
    val is_automatic: Int
        )