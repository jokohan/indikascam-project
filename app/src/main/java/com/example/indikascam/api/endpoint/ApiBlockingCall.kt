package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.PostBlockingCallRequest
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.responses.GetBlockingCallResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiBlockingCall {
    @GET("api/call/check-call/{phoneNumber}")
    suspend fun blockCallGet(
        @Header("Authorization") postTokenRequest: PostTokenRequest,
        @Path("phoneNumber") phoneNumber: String
    ):Response<GetBlockingCallResponse>

    @POST("api/detected-call-history")
    suspend fun insertBlockCall(
        @Header("Authorization") postTokenRequest: PostTokenRequest,
        @Body insertBlockCall : PostBlockingCallRequest
    ):Response<Any>
}