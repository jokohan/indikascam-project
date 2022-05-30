package com.example.indikascam.api.responses

data class PostRegisterResponse(
    val message: String,
    val data: Data
) {
    data class Data(
        val name: String,
        val email: String,
        val is_active: Int,
        val updated_at: String,
        val created_at: String,
        val id: Int
    )
}