package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.responses.GetBlockHistoryResponse
import com.example.indikascam.api.responses.GetNotificationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiNotification {

    @GET("api/detected-call-history/block-history")
    suspend fun getBlockHistory(
        @Header("Authorization") postTokenRequest: PostTokenRequest
    ): Response<GetBlockHistoryResponse>

    @GET("api/notification")
    suspend fun getNotification(
        @Header("Authorization") postTokenRequest: PostTokenRequest
    ): Response<GetNotificationResponse>

}