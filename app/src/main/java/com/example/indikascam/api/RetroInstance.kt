package com.example.indikascam.api

import com.example.indikascam.api.BaseUrl.Companion.BASE_URL
import com.example.indikascam.api.endpoint.ApiAuth
import com.example.indikascam.api.endpoint.ApiProfile
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetroInstance {

    val apiAuth: ApiAuth by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiAuth::class.java)
    }

    val apiProfile: ApiProfile by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiProfile::class.java)
    }

}