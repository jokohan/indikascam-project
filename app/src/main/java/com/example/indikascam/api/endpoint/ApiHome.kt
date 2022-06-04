package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.requests.PostUserBlockRequest
import com.example.indikascam.api.responses.GetSearchPhoneNumberResponse
import com.example.indikascam.api.responses.PostUserBlockResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiHome {

    @GET("api/user-report/phone-number/{phone_number}")
    suspend fun getSearchPhoneNumber(
        @Header("Authorization") token: PostTokenRequest,
        @Path("phone_number") phoneNumber: String
    ): Response<GetSearchPhoneNumberResponse>

    @POST("api/user-block")
    suspend fun postUserBlock(
        @Header("Authorization") token:PostTokenRequest,
        @Body postUserBlockRequest: PostUserBlockRequest
    ): Response<PostUserBlockResponse>

}