package com.heremanikandan.scriptifyevents.drawer.event

import android.app.TimePickerDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.db.ScriptyManager
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.model.Reminder
import com.heremanikandan.scriptifyevents.ui.theme.Yellow60
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager
import com.heremanikandan.scriptifyevents.viewModel.HomeViewModel
import com.heremanikandan.scriptifyevents.viewModel.factory.HomeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    var reminderTime by remember { mutableStateOf(today.time) }
    var eventNameError by remember { mutableStateOf<String?>(null) }
    var eventDateError by remember { mutableStateOf<String?>(null) }
    var reminderDateError by remember { mutableStateOf<String?>(null) }
    val localDbEvent  = ScriptyManager.getInstance(context).EventDao()
    val localReminder = ScriptyManager.getInstance(context).ReminderDao()
    val sharedPrefManager = SharedPrefManager(context)
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(localDbEvent))
    val coroutineScope = rememberCoroutineScope()

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

    suspend fun validateName(name: String?): Boolean {
    val regex = Regex("^[a-zA-Z0-9_]{2,}\$")

    if (name.isNullOrBlank()) {
        eventNameError = "Event name cannot be empty"
        return false
    }

    if (!regex.matches(name)) {
        eventNameError = "Event name must have at least 2 characters"
        return false
    }

    val exists = viewModel.isEventNameExists(name)
    if (exists) {
        eventNameError = "Name already exists"
        return false
    }

    eventNameError = null
    return true
}

    fun convertToMillis(dateStr: String, timeStr: String): Long {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

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

    fun showToast(context: Context, message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    suspend fun getEventByName(eventName: String): Event? {
        return withContext(Dispatchers.IO) {
            localDbEvent.getEventByName(eventName) // Call the DAO function inside Dispatchers.IO
        }
    }
    suspend fun validateAndSaveEvent() {
        validateEventDate(eventDate)
       if (!validateName(eventName)) return

        if (isReminderEnabled) {
            validateReminderDate(reminderDate)

        }

        eventDateError =null
        eventNameError=null
        val dateStr = dateFormatter.format(eventDate)
        val timeStr = timeFormatter.format(eventTime)
        val dateTimeMillis = convertToMillis(dateStr,timeStr)

        if (eventDateError == null && reminderDateError == null && eventNameError == null) {
            Log.d("TIME IN MILLIS ","date time in millis $dateTimeMillis")
            val newEvent= Event(
                name =  eventName,
                description = description,
                dateTimeMillis = dateTimeMillis,
                disabled = false,
                isCompleted = false,
                isOngoing = false,
                isWaiting = true,
                reminder = isReminderEnabled,
                createdBy = sharedPrefManager.getUserName()!!
            )

            viewModel.insertEvent(newEvent) { isSuccess ->
                Handler(Looper.getMainLooper()).post {

                    if (isSuccess) {
                        val reminderDateStr = dateFormatter.format(reminderDate)
                        val remindertimeStr = timeFormatter.format(reminderTime)
                        val reminderTimeMillis = convertToMillis(reminderDateStr,remindertimeStr)

                        coroutineScope.launch(Dispatchers.IO) {
                            val event = getEventByName(eventName) // This function is already using Dispatchers.IO
                            event?.let {
                                val reminderId = localReminder.insertReminder(
                                    Reminder(
                                        eventId = it.id,
                                        reminderTimeMillis = reminderTimeMillis
                                    )
                                )
                                withContext(Dispatchers.Main) {
                                    if (reminderId != null) {
                                        showToast(context, "Event and reminder added successfully")
                                    }
                                    navController.popBackStack() // Ensure it's called in Main Thread
                                }
                            }
                        }

                       // navController.popBackStack() // Ensure it's called in Main Thread
                    } else {
                        Toast.makeText(context, "Failed to add event.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // navController.popBackStack()
        }
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
//            event name
            OutlinedTextField(
                value = eventName,
                onValueChange =
                {
                    eventName = it
                   coroutineScope.launch {
                       validateName(it)
                   }
                },
                label = { Text("Event Name", color = MaterialTheme.colorScheme.onTertiary) },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onTertiary),
                isError = eventNameError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            eventNameError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
// event description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", color = MaterialTheme.colorScheme.onTertiary) },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onTertiary),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray
                )

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
//                reminder date
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
                Spacer(modifier = Modifier.width(8.dp))
//                reminder time
                TimePickerField(
                    label = "Reminder Time",
                    selectedTime = reminderTime,
                    onTimeSelected = { reminderTime = it },

                    )
            }

            Button(
                onClick = { coroutineScope.launch {
                    validateAndSaveEvent()
                } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Yellow60,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )

            ) {
                Text("Save Event")
            }
        }
    }
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

