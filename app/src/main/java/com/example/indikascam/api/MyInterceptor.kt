package com.example.indikascam.api

import android.content.Context
import com.example.indikascam.sessionManager.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor(context: Context): Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.fetchAuthToken()?.let{
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}