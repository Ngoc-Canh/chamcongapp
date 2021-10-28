package com.example.demo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        createChannelNotification()
        retrieveTokenDevice()
    }

    private fun createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID, "PushNotification",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            println(CHANNEL_ID)
        }
    }

    private fun retrieveTokenDevice(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            task -> if(task.isSuccessful){
                val token: String? = task.result
                println("Token device is: $token")
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "push_notification_id"
    }
}