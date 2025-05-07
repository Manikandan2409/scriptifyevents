package com.heremanikandan.scriptifyevents.components

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun DatePickerField(label: String, selectedDate: Date, onDateSelected: (Date) -> Unit, errorMessage: String?, boxColor: Color, textColor: Color) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    OutlinedTextField(
        value = dateFormatter.format(selectedDate),
        onValueChange = {},
        label = { Text(label, color = MaterialTheme.colorScheme.onTertiary) },
        readOnly = true,
        isError = errorMessage != null,
        trailingIcon = {
            IconButton(onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val newDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time
                        onDateSelected(newDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick date", tint = MaterialTheme.colorScheme.onTertiary)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
    )
    errorMessage?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }
}

@Composable
fun TimePickerField(label: String, selectedTime: Date, onTimeSelected: (Date) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { time = selectedTime }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    OutlinedTextField(
        value = timeFormatter.format(selectedTime),
        onValueChange = {},
        label = { Text(label, color = MaterialTheme.colorScheme.onTertiary) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val newTime = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }.time
                        onTimeSelected(newTime)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()
            }) {
                Icon(Icons.Default.AccessTime, contentDescription = "Pick time", tint = MaterialTheme.colorScheme.onTertiary)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
    )
}


fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }
}
fun isExactAlarmPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        // No need to request permission for versions below Android 12 (S)
        true
    }

}

