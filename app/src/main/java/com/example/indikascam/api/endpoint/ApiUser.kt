package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.PostFileRequest
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.requests.PostUserConfiguration
import com.example.indikascam.api.responses.GetMyReportDetailResponse
import com.example.indikascam.api.responses.GetMyReportsResponse
import com.example.indikascam.api.responses.GetMyReviewResponse
import com.example.indikascam.api.responses.ResponseWithOnlyOneMessage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiUser {

    @Multipart
    @POST("api/user/profile?_method=PATCH")
    suspend fun postEditProfile(
        @Header("Authorization") token: PostTokenRequest,
        @Part ("name") name: RequestBody,
        @Part ("remove_profile_picture") remove_profile_picture: Int,
        @Part ("bank_account_number") bank_account_number: RequestBody?,
        @Part ("bank_id") bank_id: Int?,
        @Part ("phone_number") phone_number: RequestBody?,
        @Part profile_picture: MultipartBody.Part?
    ) : Response<Any>

    @POST("api/user/configuration?_method=PATCH")
    suspend fun postUserConfiguration(
        @Header("Authorization") token: PostTokenRequest,
        @Body postUserConfiguration: PostUserConfiguration
    ) : Response<ResponseWithOnlyOneMessage>

    @POST("api/file")
    suspend fun postFile(
        @Header("Authorization") token: PostTokenRequest,
        @Body postFile: PostFileRequest
    ): Response<ResponseBody>

    @GET("api/user-report/my-reports")
    suspend fun getMyReports(
        @Header("Authorization") token: PostTokenRequest
    ): Response<GetMyReportsResponse>

    @GET("api/user-report/my-report/{report_id}")
    suspend fun getMyReportDetail(
        @Header("Authorization") token: PostTokenRequest,
        @Path("report_id") reportId: String
    ): Response<GetMyReportDetailResponse>

    @GET("api/review-request/my-request")
    suspend fun getMyReview(
        @Header("Authorization") token: PostTokenRequest,
    ): Response<GetMyReviewResponse>
}