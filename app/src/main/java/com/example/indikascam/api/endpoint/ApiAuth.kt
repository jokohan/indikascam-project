package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.PostEmailVerificationRequest
import com.example.indikascam.api.requests.PostLoginRequest
import com.example.indikascam.api.requests.PostRegisterRequest
import com.example.indikascam.api.responses.ResponseWithOnlyOneMessage
import com.example.indikascam.api.responses.PostLoginResponse
import com.example.indikascam.api.responses.PostRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiAuth {

//    @POST("api/auth/refresh")
//    suspend fun postRefresh(
//        @Header("Authorization") token: String,
//    ): Response<PostRefreshResponse>

    @POST("api/auth/login")
    suspend fun postLogin(
        @Body postLoginRequest: PostLoginRequest
    ): Response<PostLoginResponse>

    @POST("api/auth/register")
    suspend fun postRegister(
        @Body postRegisterRequest: PostRegisterRequest
    ): Response<PostRegisterResponse>

    @POST("api/auth/email-verification")
    suspend fun postEmailVerification(
        @Header("Authorization") token: String,
        @Body postEmailVerification: PostEmailVerificationRequest
    ): Response<ResponseWithOnlyOneMessage>

    @POST("api/auth/send-email-token")
    suspend fun postResendEmailToken(
        @Header("Authorization") token: String
    ): Response<ResponseWithOnlyOneMessage>

    @POST("api/auth/forget-password")
    suspend fun postForgetPassword(
        @Body email: String
    ): Response<Any>

//    @POST("api/auth/change-password-verification")
//    suspend fun postChangePasswordVerification(
//        @Body postChangePasswordVerificationRequest: PostChangePasswordVerificationRequest
//    ): Response<Any>

//    @POST("api/auth/change-password")
//    suspend fun postChangePassword(
//        @Body postChangePasswordRequest: PostChangePasswordRequest
//    ): Response<Any>

    @POST("api/auth/logout")
    suspend fun postLogout(
        @Header("Authorization") token: String
    ): Response<Any>

}