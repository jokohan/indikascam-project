package com.example.indikascam.api

import com.example.indikascam.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface SimpleApi {
    @POST("api/auth/login")
    suspend fun pushPost(
        @Body post: Post
    ): Response<Any>

    @POST("api/auth/register")
    suspend fun registerPost(
        @Body registerPost: RegisterPost
    ): Response<Any>

    @POST("api/auth/email-verification")
    suspend fun emailVerificationPost(
        @Header ("Authorization") token: String,
        @Body emailVerification: EmailVerificationPost
    ): Response<Any>

    @POST("api/auth/forget-password")
    suspend fun forgotPasswordPost(
        @Body forgotPassword: ForgotPasswordPost
    ): Response<Any>

    @POST("api/auth/change-password-verification")
    suspend fun changePasswordTokenPost(
        @Body changePasswordToken: ChangePasswordTokenPost
    ): Response<Any>

    @POST("api/auth/change-password")
    suspend fun newPasswordPost(
        @Body newPassword: NewPasswordPost
    ): Response<Any>

    @POST("api/auth/logout")
    suspend fun logoutPost(
        @Header ("Authorization") token: String
    ): Response<Any>

    @POST("api/auth/send-email-token")
    suspend fun resendEmailTokenPost(
        @Header ("Authorization") token: String
    ):Response<Any>

    @GET("api/auth/me")
    suspend fun meGet(
        @Header ("Authorization") token: String
    ): Response<Any>

    @GET("api/platform?limit=all")
    suspend fun platformsGet():Response<Any>

    @GET("api/product?limit=all")
    suspend fun productsGet():Response<Any>

    @GET("api/bank?limit=all")
    suspend fun banksGet():Response<Any>

    @GET("api/report-type?limit=all")
    suspend fun reportTypeGet():Response<Any>

    @Multipart
    @POST("api/user-report")
    suspend fun userReportPost(
        @Header("Authorization") token: String,
        @Part ("report_type_id") jenisGangguan: Int,
        @Part ("bank_id") bank: Int,
        @Part ("bank_account_number") noRek: String,
        @Part ("scammer_name") namaPenipu: String,
        @Part ("platform_id") platform: Int,
        @Part ("product_id") product: Int,
        @Part ("chronology") kronologi: String,
        @Part bukti: List<MultipartBody.Part>,
        @Part ("total_loss") totalKerugian: Int,
        @Part ("scammer_phone_number") noTelPenipu: String
    ):Response<Any>

    @GET("fact?max_length=140")
    fun getCatFacts(): Call<testing>
}