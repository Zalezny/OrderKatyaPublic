/*
package com.example.orderappkatya.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.orderappkatya.ItemActivity
import com.example.orderappkatya.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService() {

    private val TAG = "FireBaseMessagingService"
    var NOTIFICATION_CHANNEL_ID = "net.katya.notification"
    val NOTIFICATION_ID = 100

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("message", "Message Received...")
        if(message.data.isNotEmpty()) {
            val body = message.data["body"]
            showNotification(applicationContext, body!!)
        } else {
            val body = message.notification!!.body
            showNotification(applicationContext, body!!)
        }

    }

    private fun showNotification(context: Context, body: String) {
        val intent = Intent(this, ItemActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("body", body)
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, 0)
        }

        val bitmap =
            BitmapFactory.decodeResource(applicationContext.resources, R.drawable.katya_logo)
        val bitmapLargeIcon =
            BitmapFactory.decodeResource(applicationContext.resources, R.drawable.katya_logo)


        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setColor(resources.getColor(R.color.red_light))
            .setContentTitle("Nowe zamówienie")
            .setContentText("Właśnie wleciało nowe zamówienie")
            .setLargeIcon(bitmapLargeIcon)
//            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(pendingIntent) // go here after click in notification
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

}*/
