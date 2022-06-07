package com.example.indikascam.api

import com.example.indikascam.api.BaseUrl.Companion.BASE_URL
import com.example.indikascam.api.endpoint.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetroInstance {

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

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
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiReport::class.java)
    }

    val apiHome: ApiHome by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiHome::class.java)
    }

    val apiBlockingCall: ApiBlockingCall by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiBlockingCall::class.java)
    }

    val apiNotification: ApiNotification by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiNotification::class.java)
    }

}
