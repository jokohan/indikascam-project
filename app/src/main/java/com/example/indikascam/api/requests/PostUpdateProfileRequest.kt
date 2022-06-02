package com.example.indikascam.api.requests

import okhttp3.MultipartBody
import retrofit2.http.Part

data class PostUpdateProfileRequest (
    val name: String,
    val remove_profile_picture: Int,
    val bank_account_number: String,
    val bank_id: Int,
    @Part val profile_picture: MultipartBody.Part
    )