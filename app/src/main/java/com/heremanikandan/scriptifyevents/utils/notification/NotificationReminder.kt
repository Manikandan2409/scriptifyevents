package com.heremanikandan.scriptifyevents.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.heremanikandan.scriptifyevents.MainActivity
import com.heremanikandan.scriptifyevents.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "Reminder"
        val message = intent?.getStringExtra("message") ?: "Your scheduled notification."

        Log.d("NotificationReceiver", "Received alarm: $title - $message")

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

        val bitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.logo // Replace with your PNG file name (without extension)
        )
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo) // Replace with your icon
            .setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)

        notificationManager.notify(notificationId, builder.build())
    }
}