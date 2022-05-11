package com.example.indikascam.services

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.example.indikascam.api.SimpleApi
import com.example.indikascam.model.insertBlockCallPost
import com.example.indikascam.sessionManager.SessionManager
import com.example.indikascam.util.Constants.Companion.BASE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MyCallScreeningService : CallScreeningService() {

    private val TAG = "CallScreeningService"

    val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .build()

    val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(SimpleApi::class.java)

    override fun onScreenCall(p0: Call.Details) {
        val startTime = p0.creationTimeMillis
        //0 == panggilan masuk, 1 ==  panggilan keluar
        if (p0.callDirection == 0) {
            val phoneNumber = getPhoneNumber(p0)
            callApi(p0, phoneNumber, startTime)
        }
    }

    private fun callApi(p0: Call.Details, phoneNumber: String, startTime: Long) {
        val sessionManager = SessionManager(applicationContext)
        var accessToken = sessionManager.fetchAuthToken()
        var timeout: Long = System.currentTimeMillis() - startTime

        Log.d(TAG, "Waktu: ${(timeout)}ms")
        if (accessToken == null) {
            //blm pernah login
            respondToCall(p0, CallResponse.Builder().build())
        } else {
            //sudah pernah login
            Log.d(TAG, "Token lama: $accessToken")

            GlobalScope.launch(Dispatchers.IO) {
                //refresh token
                timeout = System.currentTimeMillis() - startTime
                Log.d(TAG, "Waktu: ${(timeout)}ms")
                val responseRefreshToken =
                    api.refreshTokenPost("Bearer $accessToken").awaitResponse()
                if (responseRefreshToken.isSuccessful) {
                    val data = responseRefreshToken.body()!!
                    sessionManager.saveAuthToken(
                        data.access_token!!
                    )
                    accessToken = sessionManager.fetchAuthToken()
                    Log.d(TAG, "Token Baru: $accessToken!!")
                    timeout = System.currentTimeMillis() - startTime
                    Log.d(TAG, "Waktu: ${(timeout)}ms")
                    //Block Call
                    val responseToBlockCall =
                        api.blockCallGet("Bearer $accessToken", phoneNumber).awaitResponse()
                    if (responseToBlockCall.isSuccessful) {
                        val data = responseToBlockCall.body()!!
                        Log.d(TAG, "Keputusan: ${data.block.toString()}")
                        if (data.block!!) {
                            timeout = System.currentTimeMillis() - startTime
                            Log.d(TAG, "Waktu: ${timeout.toString()}")
                            if (timeout >= 5000) {
                                Log.d(TAG, "timeout gak sempat blokir")
                            } else {
                                Log.d(TAG, "Lanjut blokir")
                                respondToCall(p0, CallResponse.Builder().apply {
                                    //end call scammer
                                    setRejectCall(true)
                                    //user tidak mendapatkan panggilan masuk
                                    setDisallowCall(true)
                                }.build())
                                val insertBlockCallPost = insertBlockCallPost(phoneNumber, 1)
                                val responseInsertBlockCall =
                                    api.insertBlockCall("Bearer $accessToken", insertBlockCallPost)
                                        .awaitResponse()
                                if (responseInsertBlockCall.isSuccessful) {
                                    Log.d(TAG, "tercatat")
                                }
                            }
                        } else {
                            respondToCall(p0, CallResponse.Builder().build())
                        }

                    } else {
                        Log.d(TAG, responseToBlockCall.code().toString())
                        val reader = responseToBlockCall.errorBody()?.byteStream()
                        Log.d(TAG, reader.toString())
                    }
                } else {
                    Log.d(TAG, responseRefreshToken.code().toString())
                    val reader = responseRefreshToken.errorBody()?.byteStream()
                    Log.d(TAG, reader.toString())
                }

            }
        }
    }

    private fun getPhoneNumber(p0: Call.Details): String {
        val removeTelPrefix = p0.handle.toString().replace("tel:", "")
        return Uri.decode(removeTelPrefix)
    }
}