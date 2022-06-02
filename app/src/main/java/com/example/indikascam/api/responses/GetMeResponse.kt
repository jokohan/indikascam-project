package com.example.indikascam.api.responses

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

data class GetMeResponse(
    val id: Int,
    val name: String,
    val email: String,
    val phone_number: String?,
    val bank_account_number: String?,
    val bank_id: Int?,
    val profile_picture: String?,
    val is_active: Int,
    val email_verified_at: String,
    val is_anonymous: Int,
    val protection_level: Int,
    val created_at: String,
    val updated_at: String
)