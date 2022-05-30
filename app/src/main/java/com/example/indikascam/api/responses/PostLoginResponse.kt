package com.example.indikascam.api.responses

data class PostLoginResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val user: User
) {
    data class User(
        val id: Int,
        val name: String,
        val email: String,
        val phone_number: String,
        val bank_id: Int?,
        val bank_account_number: String?,
        val profile_picture: String?,
        val is_active: Int,
        val email_verified_at: String?,
        val protection_level: Int,
        val is_anonymous: Int,
        val created_at: String,
        val updated_at: String
    )
}