package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.*
import com.example.indikascam.api.responses.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiAuth {

    @POST("api/auth/refresh")
    suspend fun postRefresh(
        @Header("Authorization") postTokenRequest: PostTokenRequest,
    ): Response<PostRefreshResponse>

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
        @Body postForgetPassword: PostForgetPassword
    ): Response<ResponseWithOnlyOneMessage>

    @POST("api/auth/change-password-verification")
    suspend fun postChangePasswordVerification(
        @Body postChangePasswordVerificationRequest: PostChangePasswordVerificationRequest
    ): Response<ResponseWithOnlyOneMessage>

    @POST("api/auth/change-password")
    suspend fun postChangePassword(
        @Body postChangePasswordRequest: PostChangePasswordRequest
    ): Response<ResponseWithOnlyOneMessage>

    @POST("api/auth/logout")
    suspend fun postLogout(
        @Header("Authorization") postLogoutRequest: PostTokenRequest
    ): Response<ResponseWithOnlyOneMessage>

    @GET("api/auth/me")
    suspend fun getMe(
        @Header("Authorization") getMeRequest: GetMeRequest
    ): Response<GetMeResponse>

}