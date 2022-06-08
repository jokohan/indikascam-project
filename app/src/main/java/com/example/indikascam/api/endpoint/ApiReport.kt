package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.responses.BankNameByIdResponse
import com.example.indikascam.api.responses.BanksAndProductsResponse
import com.example.indikascam.api.responses.PlatformsAndReportTypeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiReport {

    @GET("api/bank?active=1&limit=all")
    suspend fun getBank(
        @Header("Authorization") token: PostTokenRequest
    ): Response<BanksAndProductsResponse>

    @GET("api/product?active=1&limit=all")
    suspend fun getProduct(
        @Header("Authorization") token: PostTokenRequest
    ): Response<BanksAndProductsResponse>

    @GET("api/platform")
    suspend fun getPlatform(
        @Header("Authorization") token: PostTokenRequest
    ): Response<PlatformsAndReportTypeResponse>

    @GET("api/report-type")
    suspend fun getReportType(
        @Header("Authorization") token: PostTokenRequest
    ): Response<PlatformsAndReportTypeResponse>

    @GET("api/bank/{bank_id}")
    suspend fun getBankNameById(
        @Header("Authorization") token: PostTokenRequest,
        @Path("bank_id") bankId: Int
    ): Response<BankNameByIdResponse>

    @Multipart
    @POST("api/user-report")
    suspend fun postReport(
        @Header("Authorization") token: PostTokenRequest,
        @Part("report_type_id") reportType: Int,
        @Part("bank_id") bankId: Int?,
        @Part("bank_account_number") accountNumber: RequestBody?,
        @Part("scammer_name") scammerName: RequestBody?,
        @Part("platform_id") platformId: Int?,
        @Part("product_id") productId: Int?,
        @Part("chronology") chronology: RequestBody?,
        @Part file: List<MultipartBody.Part>?,
        @Part("total_loss") totalLost: Int?,
        @Part("scammer_phone_number") phoneNumber: RequestBody?
    ):Response<Any>

}