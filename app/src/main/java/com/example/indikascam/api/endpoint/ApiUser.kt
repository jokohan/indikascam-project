package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.requests.PostUserConfiguration
import com.example.indikascam.api.responses.ResponseWithOnlyOneMessage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiUser {

    @Multipart
    @POST("api/user/profile?_method=PATCH")
    suspend fun postEditProfile(
        @Header("Authorization") token: PostTokenRequest,
        @Part ("name") name: RequestBody,
        @Part ("remove_profile_picture") remove_profile_picture: Int,
        @Part ("bank_account_number") bank_account_number: RequestBody,
        @Part ("bank_id") bank_id: Int,
        @Part ("phone_number") phone_number: RequestBody,
        @Part profile_picture: MultipartBody.Part?
    ) : Response<Any>

    @POST("api/user/configuration?_method=PATCH")
    suspend fun postUserConfiguration(
        @Header("Authorization") token: PostTokenRequest,
        @Body postUserConfiguration: PostUserConfiguration
    ) : Response<ResponseWithOnlyOneMessage>

}