package com.example.indikascam.api.endpoint

import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.responses.BankNameById
import com.example.indikascam.api.responses.Banks
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiReport {

    @GET("api/bank?active=1&limit=all")
    suspend fun getBank(
        @Header("Authorization") token: PostTokenRequest
    ): Response<Banks>

    @GET("api/bank/{bank_id}")
    suspend fun getBankNameById(
        @Header("Authorization") token: PostTokenRequest,
        @Path("bank_id") bankId: Int
    ): Response<BankNameById>

}