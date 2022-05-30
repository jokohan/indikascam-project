package com.example.indikascam.services

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class MyCallScreeningService: CallScreeningService() {

    private val debugTag = "CallScreeningService"
    override fun onScreenCall(p0: Call.Details) {
        val startTime = p0.creationTimeMillis

        /*
            0 == incoming call,
            1== outgoing call
        */

        if (p0.callDirection == 0) {
            val phoneNumber = getPhoneNumber(p0)
        }
    }



    private fun getPhoneNumber(p0: Call.Details): Any {
        val removeTelPrefix = p0.handle.toString().replace("tel:", "")
        return Uri.decode(removeTelPrefix)
    }
}