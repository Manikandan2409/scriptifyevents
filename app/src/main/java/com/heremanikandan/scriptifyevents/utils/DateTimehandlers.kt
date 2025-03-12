package com.heremanikandan.scriptifyevents.utils


import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
     fun  convertToMillis(dateStr: String, timeStr: String): Long {

        val date = dateFormatter.parse(dateStr) // Parse date
        val time = timeFormatter.parse(timeStr) // Parse time

        val calendar = Calendar.getInstance()
        calendar.time = date ?: return -1 // Set date

        val timeCalendar = Calendar.getInstance()
        timeCalendar.time = time ?: return -1

        // Set parsed time (hour, minute) into the date calendar
        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis // Convert to milliseconds
    }


    fun convertMillisToDateTime(millis: Long): Pair<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Date format
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())    // 12-hour time format

        val date = Date(millis) // Convert millis to Date object

        val formattedDate = dateFormat.format(date) // Extract date
        val formattedTime = timeFormat.format(date) // Extract time

        return Pair(formattedDate, formattedTime) // Return as a pair of strings
    }

