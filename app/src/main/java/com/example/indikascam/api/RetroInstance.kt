package com.example.indikascam.api

import com.example.indikascam.api.BaseUrl.Companion.BASE_URL
import com.example.indikascam.api.endpoint.ApiAuth
import com.example.indikascam.api.endpoint.ApiReport
import com.example.indikascam.api.endpoint.ApiUser
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroInstance {

    val apiAuth: ApiAuth by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiAuth::class.java)
    }

    val apiProfile: ApiUser by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiUser::class.java)
    }

    val apiReport: ApiReport by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiReport::class.java)
    }

}
