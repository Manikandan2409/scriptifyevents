package com.heremanikandan.scriptifyevents.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.heremanikandan.scriptifyevents.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "Reminder"
        val message = intent?.getStringExtra("message") ?: "Your scheduled notification."

        // Call function to show the notification
        context?.let {
            showNotification(it, title, message)
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val channelId = "notification_channel"
        val notificationId = System.currentTimeMillis().toInt()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Scheduled Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo) // Replace with your icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(notificationId, builder.build())

    }
}