package com.example.indikascam.model

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

data class LaporPost (
    val report_type_id: Int,
    val bank_id: Int,
    val bank_account_number: String,
    val scammer_name: String,
    val platform_id: Int,
    val product_id: Int,
    val chronology: String,
    @Part val files: List<MultipartBody.Part>,
    val total_loss: Int,
    val scammer_phone_number: String
    )
