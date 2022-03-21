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
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.indikascam.MainActivity
import com.example.indikascam.R

private val CHANNEL_ID = "channel_id_scammer"
private val notificationId = 101

class MyCallScreeningService : CallScreeningService() {

    override fun onScreenCall(p0: Call.Details) {
        val phoneNumber = getPhoneNumber(p0)
        var response = CallResponse.Builder()

        response = handlePhoneCall(response, phoneNumber)

        respondToCall(p0, response.build())
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