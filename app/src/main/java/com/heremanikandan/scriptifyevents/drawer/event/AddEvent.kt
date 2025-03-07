package com.heremanikandan.scriptifyevents.drawer.event

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun AddEvent(
   navController: NavController
) {

    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val today = remember { Calendar.getInstance() }
    val maxEventDate = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 30) } }

    var eventName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf(today.time) }
    var eventTime by remember { mutableStateOf(today.time) }
    var isReminderEnabled by remember { mutableStateOf(false) }
    var reminderDate by remember { mutableStateOf(today.time) }

    var eventDateError by remember { mutableStateOf<String?>(null) }
    var reminderDateError by remember { mutableStateOf<String?>(null) }

    fun validateEventDate(date: Date) {
        val selectedDate = Calendar.getInstance().apply { time = date }
        eventDateError = when {
            selectedDate.before(today) -> "Event date cannot be in the past"
            selectedDate.after(maxEventDate) -> "Event date cannot be beyond 30 days from today"
            else -> null
        }
    }

    fun validateReminderDate(date: Date) {
        val selectedReminder = Calendar.getInstance().apply { time = date }
        val selectedEventDate = Calendar.getInstance().apply { time = eventDate }
        reminderDateError = when {
            selectedReminder.before(today) -> "Reminder cannot be in the past"
            selectedReminder.after(selectedEventDate) -> "Reminder must be before event date"
            else -> null
        }
    }

    fun validateAndSaveEvent() {
        validateEventDate(eventDate)
        if (isReminderEnabled) validateReminderDate(reminderDate)

        if (eventDateError == null && reminderDateError == null) {
            Toast.makeText(context, "Event saved successfully!", Toast.LENGTH_SHORT).show()
        }
        navController.popBackStack()
    }

    // Screen Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                label = "Event Name",
                value = eventName,
                onValueChange = { eventName = it },

            )

            CustomTextField(
                label = "Description",
                value = description,
                onValueChange = { description = it },

            )

            DatePickerField(
                label = "Event Date",
                selectedDate = eventDate,
                onDateSelected = {
                    eventDate = it
                    validateEventDate(it)
                },
                errorMessage = eventDateError,
                boxColor = MaterialTheme.colorScheme.secondary,
                textColor = MaterialTheme.colorScheme.onTertiary
            )

            TimePickerField(
                label = "Event Time",
                selectedTime = eventTime,
                onTimeSelected = { eventTime = it },

            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Do you want to add a reminder?", color = MaterialTheme.colorScheme.onTertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isReminderEnabled,
                    onCheckedChange = { isReminderEnabled = it }
                )
            }

            if (isReminderEnabled) {
                DatePickerField(
                    label = "Reminder Date",
                    selectedDate = reminderDate,
                    onDateSelected = {
                        reminderDate = it
                        validateReminderDate(it)
                    },
                    errorMessage = reminderDateError,
                    boxColor = MaterialTheme.colorScheme.secondary,
                    textColor = MaterialTheme.colorScheme.onTertiary
                )
            }

            Button(
                onClick = { validateAndSaveEvent() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Event")
            }
        }
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, ) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = MaterialTheme.colorScheme.onTertiary) },
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onTertiary),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.LightGray
        )
    )
}

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
                android.app.DatePickerDialog(
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

