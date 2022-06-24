package com.example.indikascam.services

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostBlockingCallRequest
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.sessionManager.SessionManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.*

@DelicateCoroutinesApi
class MyCallScreeningService : CallScreeningService() {

    private val debugTag = "CallScreeningService"

    override fun onScreenCall(p0: Call.Details) {
        val startCallCreationTime = p0.creationTimeMillis

        /*
            0 == panggilan masuk,
            1 == panggilan keluar
        */

        if (p0.callDirection == 0) {
            val phoneNumber = getPhoneNumber(p0)
            Log.i(debugTag, phoneNumber)
            callApi(p0, phoneNumber, startCallCreationTime)
        }
    }

    private fun callApi(p0: Call.Details, phoneNumber: String, startCallCreationTime: Long) {
        val sessionManager = SessionManager(applicationContext)
        var accessToken = sessionManager.fetchAuthToken()
        if (sessionManager.fetchAuthToken().isNullOrEmpty()){
            Log.i(debugTag, "Belum Login")
            respondToCall(p0, CallResponse.Builder().build())
        }else{
            if(sessionManager.fetchExpireToken() - Date().time <= 0){
                GlobalScope.launch(Dispatchers.IO) {
                    //request refresh token
                    val responseRefreshToken = try {
                        RetroInstance.apiAuth.postRefresh(PostTokenRequest("Bearer $accessToken"))
                    } catch (e: IOException) {
                        Log.e(debugTag, e.message!!)
                        return@launch
                    } catch (e: HttpException) {
                        Log.e(debugTag, e.message!!)
                        return@launch
                    }
                    if (responseRefreshToken.isSuccessful && responseRefreshToken.body() != null) {
                        sessionManager.saveAuthToken(responseRefreshToken.body()!!.access_token)
                        sessionManager.saveExpireToken(responseRefreshToken.body()!!.expires_in * 1000 + Date().time)
                        accessToken = sessionManager.fetchAuthToken()
                        blockCall(p0, phoneNumber, startCallCreationTime, accessToken)
                        Log.i(debugTag, "Berhasil refresh")
                    }else{
                        try{
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(responseRefreshToken.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("meError", errorMessage)
                            Log.e("meError", responseRefreshToken.code().toString())
                        }catch (e: Exception){
                            Log.e("meError", e.toString())
                        }
                    }
                }
            }else{
                blockCall(p0, phoneNumber, startCallCreationTime, accessToken)
            }
        }
    }

    private fun blockCall(
        p0: Call.Details,
        phoneNumber: String,
        startCallCreationTime: Long,
        accessToken: String?
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            //request blocking call api
            val responseBlockingCall = try{
                RetroInstance.apiBlockingCall.blockCallGet(PostTokenRequest("Bearer $accessToken"), phoneNumber)
            } catch (e: IOException) {
                Log.e(debugTag, e.message!!)
                return@launch
            } catch (e: HttpException) {
                Log.e(debugTag, e.message!!)
                return@launch
            }
            if(responseBlockingCall.isSuccessful && responseBlockingCall.body() != null){
                if(responseBlockingCall.body()!!.block){
                    respondToCall(p0, CallResponse.Builder().apply {
                        //end call for scammer
                        setRejectCall(true)
                        //user tidak mendapatkan panggilan masuk
                        setDisallowCall(true)
                    }.build())
                    if(System.currentTimeMillis() - startCallCreationTime < 4900){
                        Log.i(debugTag, "$phoneNumber berhasil di blokir")
                        val responseInsertBlockCall = try{
                            RetroInstance.apiBlockingCall.insertBlockCall(PostTokenRequest("Bearer $accessToken"),PostBlockingCallRequest(phoneNumber,1, if(responseBlockingCall.body()!!.is_automatic)1 else 0))
                        } catch (e: IOException) {
                            Log.e(debugTag, e.message!!)
                            return@launch
                        } catch (e: HttpException) {
                            Log.e(debugTag, e.message!!)
                            return@launch
                        }
                        if(responseInsertBlockCall.isSuccessful && responseInsertBlockCall.body() != null){
                            Log.i(debugTag, "Berhasil di simpan")
                        }else{
                            try{
                                @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(responseBlockingCall.errorBody()!!.string())
                                val errorMessage = jObjError.getJSONObject("error").getString("message")
                                Log.e("meError", errorMessage)
                                Log.e("meError", responseBlockingCall.code().toString())
                            }catch (e: Exception){
                                Log.e("meError", e.toString())
                            }
                        }
                    }
                }else{
                    respondToCall(p0, CallResponse.Builder().build())
                }
            }else{
                try{
                    @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(responseBlockingCall.errorBody()!!.string())
                    val errorMessage = jObjError.getJSONObject("error").getString("message")
                    Log.e("meError", errorMessage)
                    Log.e("meError", responseBlockingCall.code().toString())
                }catch (e: Exception){
                    Log.e("meError", e.toString())
                }
            }
        }
    }

    private fun getPhoneNumber(p0: Call.Details): String {
        val removeTelPrefix = p0.handle.toString().replace("tel:", "")
        return Uri.decode(removeTelPrefix)
    }
}