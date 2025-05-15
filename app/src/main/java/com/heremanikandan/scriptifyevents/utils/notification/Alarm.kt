package com.heremanikandan.scriptifyevents.utils.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

fun scheduleNotification(
    context: Context,
    title: String,
    message: String,
    timeInMillis: Long,
    requestCode: Int = (timeInMillis / 1000).toInt() // Default unique requestCode
) {
    if (timeInMillis < System.currentTimeMillis()) return // Avoid scheduling in the past

    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("title", title)
        putExtra("message", message)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    if (timeInMillis <= System.currentTimeMillis()) {
        Log.w("AlarmKt", "Skipping scheduling for past time: $timeInMillis")
        return
    }
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
}


