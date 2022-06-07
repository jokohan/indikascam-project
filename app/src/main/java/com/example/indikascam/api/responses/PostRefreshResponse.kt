package com.example.indikascam.api.responses

data class PostRefreshResponse (
    val access_token: String,
    val token_type: String,
    val expires_in: Long
        )