package com.example.indikascam.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.indikascam.MainActivity
import com.example.indikascam.MainViewModel
import com.example.indikascam.R
import com.example.indikascam.api.SimpleApi
import com.example.indikascam.util.Constants.Companion.BASE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

private val CHANNEL_ID = "channel_id_scammer"
private val notificationId = 101

class MyCallScreeningService : CallScreeningService() {

    override fun onScreenCall(p0: Call.Details) {
        val phoneNumber = getPhoneNumber(p0)
        callApi(p0, phoneNumber)
    }

    private fun callApi(p0: Call.Details, phoneNumber: String) {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SimpleApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCatFacts().awaitResponse()
                if (response.isSuccessful){
                    val data = response.body()!!
                    Log.d("TAG", data.fact)

                    withContext(Dispatchers.Main){
                        Toast.makeText(applicationContext, data.fact, Toast.LENGTH_LONG).show()
                    }
                    var responseForCall = CallResponse.Builder()
                    responseForCall = handlePhoneCall(responseForCall, phoneNumber)
                    respondToCall(p0, responseForCall.build())
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "ada yg salah", Toast.LENGTH_LONG).show()
                }
            }

        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(phoneNumber: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        intent.putExtra("a", phoneNumber)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("IndikaScam")
            .setContentText("Awas Penipu: $phoneNumber")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(applicationContext)){
            notify(notificationId, builder.build())
        }
    }

    private fun handlePhoneCall(
        response: CallResponse.Builder,
        phoneNumber: String
    ): CallResponse.Builder {
        val scammer = "+6282129993401"

//        val alert = Toast.makeText(applicationContext, "Awas Penipu!", Toast.LENGTH_LONG)
//        alert.setGravity(Gravity.CENTER, 0, 0)
//        createNotificationChannel()

        if (scammer == phoneNumber) {
            response.apply {
                //end call scammer
                setRejectCall(true)

                //user tidak mendapatkan panggilan masuk
                setDisallowCall(true)
            }

//            sendNotification(phoneNumber)
//            alert.show()

        } else {
//            alert.setText("Telepon masuk: $phoneNumber")
//            alert.show()
        }
        return response
    }


    private fun getPhoneNumber(p0: Call.Details): String {
        val removeTelPrefix = p0.handle.toString().replace("tel:", "")
        return Uri.decode(removeTelPrefix)
    }
}