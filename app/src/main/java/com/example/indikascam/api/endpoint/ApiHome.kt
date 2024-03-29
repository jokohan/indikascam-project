package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.PostReviewRequestRequest
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.requests.PostUserBlockRequest
import com.example.indikascam.api.responses.*
import retrofit2.Response
import retrofit2.http.*

interface ApiHome {

    @GET("api/user-report/phone-number/{phone_number}")
    suspend fun getSearchPhoneNumber(
        @Header("Authorization") token: PostTokenRequest,
        @Path("phone_number") phoneNumber: String
    ): Response<GetSearchPhoneNumberResponse>

    @GET("api/user-report/bank-account-number/{account_number}")
    suspend fun getSearchAccountNumber(
        @Header("Authorization") token: PostTokenRequest,
        @Path("account_number") accountNumber: String
    ): Response<GetSearchAccountNumberResponse>

    @POST("api/user-block")
    suspend fun postUserBlock(
        @Header("Authorization") token:PostTokenRequest,
        @Body postUserBlockRequest: PostUserBlockRequest
    ): Response<PostUserBlockResponse>

    @GET("api/user-report/scam-statistics")
    suspend fun getScamStatistics(
        @Header("Authorization") token: PostTokenRequest
    ): Response<GetScamStatisticsResponse>

    @POST("api/review-request")
    suspend fun postReviewRequest(
        @Header("Authorization") token: PostTokenRequest,
        @Body postReviewRequestRequest: PostReviewRequestRequest
    ): Response<PostReviewRequestResponse>

    @GET("api/detected-call-history/block-statistic")
    suspend fun getBlockStatistic(
        @Header("Authorization") token: PostTokenRequest,
    ): Response<GetBlockStatisticsResponse>

}