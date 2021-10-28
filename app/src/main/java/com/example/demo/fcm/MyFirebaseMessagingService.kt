package com.example.demo.fcm

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.demo.MyApplication
import com.example.demo.R
import com.example.demo.backend.SessionManager
import com.example.demo.screen.LoginActivity
import com.example.demo.screen.RequestActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService: FirebaseMessagingService() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
//        if (p0.notification != null){
//            val notify = p0.notification
//            val title = notify?.title
//            val message = notify?.body
//            println(title)
//
//            sendNotification(title, message)
//        }

        if(p0.data != null){
            val data: Map<String, String> = p0.data
            val title = data["title"]
            val body = data["body"]

            sendNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("Token", "Refreshed token: $token")
    }

    private fun sendNotification(title: String?, message: String?) {
        var intent = Intent(this, RequestActivity::class.java)
        if(sessionManager.fetchMyEmail() == "null"){
            intent = Intent(this, LoginActivity::class.java)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)

        val notification = notificationBuilder.build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager != null){
            notificationManager.notify(1, notification)
        }
    }
}