package com.heremanikandan.scriptifyevents.screens.event

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.components.DatePickerField
import com.heremanikandan.scriptifyevents.components.TimePickerField
import com.heremanikandan.scriptifyevents.components.isExactAlarmPermissionGranted
import com.heremanikandan.scriptifyevents.db.ScriptyManager
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.model.EventStatus
import com.heremanikandan.scriptifyevents.sharedPref.SharedPrefManager
import com.heremanikandan.scriptifyevents.utils.convertToMillis
import com.heremanikandan.scriptifyevents.utils.notification.scheduleNotification
import com.heremanikandan.scriptifyevents.viewModel.AddEventViewModel
import com.heremanikandan.scriptifyevents.viewModel.factory.AddEventViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun AddEvent(
    navController: NavController,
     eventId:Long
) {

    val context = LocalContext.current
    val sharedPrefManager = SharedPrefManager(context)
    val localDbEvent  = ScriptyManager.getInstance(context).EventDao()
    val viewModel: AddEventViewModel = viewModel(factory = AddEventViewModelFactory(localDbEvent))
    val coroutineScope = rememberCoroutineScope()
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val today = remember { Calendar.getInstance() }
    val maxEventDate = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 30) } }
    val existingEvent = remember { mutableStateOf<Event?>(null) }

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

    // Load event from ViewModel if editing
    LaunchedEffect(key1 = eventId) {
        if (eventId != Long.MIN_VALUE) {
            val event = viewModel.getEventById(eventId)
            event?.let {
                existingEvent.value = it
                eventName = it.name
                description = it.description
                eventDate = Date(it.dateTimeMillis)
                eventTime = Date(it.dateTimeMillis)

            }
        }
    }

    val activityResultLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("Auth", "Additional permissions granted successfully")
            } else {
                Log.e("Auth", "User denied additional permissions")
            }
        }

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
        val trimmedName = name?.trim()
        val regex = Regex("^[a-zA-Z0-9_\\s]+$")

        if (trimmedName.isNullOrBlank()) {
            eventNameError = "Event name cannot be empty"
            return false
        }

        if (trimmedName.length < 2) {
            eventNameError = "Event name must have at least 2 letters"
            return false
        }

        if (trimmedName.length > 31) {
            eventNameError = "Event name must have at most 31 letters"
            return false
        }

        if (!regex.matches(trimmedName)) {
            eventNameError = "Event name must contain only letters, numbers, underscores, or spaces"
            return false
        }

        if (viewModel.isEventNameExists(trimmedName)) {
            eventNameError = "Event already exists"
            return false
        }

        eventNameError = null
        return true
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


            existingEvent.value?.let {
                    event ->
                val updatedEvent = event.copy(
                    name=eventName,
                    description = description,
                    dateTimeMillis = dateTimeMillis,
                )
                viewModel.updateEvent(updatedEvent)
                navController.popBackStack()
                return
            }
            val newEvent= Event(
                name =  eventName,
                description = description,
                dateTimeMillis = dateTimeMillis,
                status = EventStatus.WAITING,
                reminder = isReminderEnabled,
                createdBy = sharedPrefManager.getUserUid()!!,
            )

            viewModel.insertEvent(newEvent) { isSuccess ->
                Handler(Looper.getMainLooper()).post {
                    if (isSuccess) {
                        scheduleNotification(context, eventName, "Reminder for $eventName", dateTimeMillis)

                        val reminderDateStr = dateFormatter.format(reminderDate)
                        val reminderTimeStr = timeFormatter.format(reminderTime)
                        val reminderTimeMillis = convertToMillis(reminderDateStr, reminderTimeStr)
//                        coroutineScope.launch(Dispatchers.IO) {
//                            val event = getEventByName(eventName)
//                            event?.let {
////                                val reminderId = localReminder.insertReminder(
////                                    Reminder(eventId = it.id, reminderTimeMillis = reminderTimeMillis)
////                                )
//                                withContext(Dispatchers.Main) {
//                                    if (isExactAlarmPermissionGranted(context)) {
//                                        scheduleNotification(context, eventName, "Reminder for $eventName", reminderTimeMillis)
//                                        scheduleNotification(context, eventName, "Event starts now", dateTimeMillis)
//                                    }
////                                    if (reminderId != null) {
////                                        Toast.makeText(context, "Event and reminder added successfully",Toast.LENGTH_SHORT).show()
////                                    }
//                                    Toast.makeText(context, "Event Created successfully",Toast.LENGTH_SHORT).show()
////
//                                    navController.popBackStack()
//                                }
//                            }
//                        }

                        //val coroutineScope = rememberCoroutineScope()

                        coroutineScope.launch {
                            try {
                                val event = withContext(Dispatchers.IO) { getEventByName(eventName) }
                                if (event != null) {
//                                    val reminderId = withContext(Dispatchers.IO) {
//                                        localReminder.insertReminder(
//                                            Reminder(eventId = event.id, reminderTimeMillis = reminderTimeMillis)
//                                        )
//                                    }

                                    if (isExactAlarmPermissionGranted(context)) {
                                        scheduleNotification(
                                            context = context,
                                            title = eventName,
                                            message = "Reminder for $eventName",
                                            timeInMillis = reminderTimeMillis,
                                            requestCode = (reminderTimeMillis / 1000).toInt()
                                        )

                                        scheduleNotification(
                                            context = context,
                                            title = eventName,
                                            message = "Event starts now",
                                            timeInMillis = dateTimeMillis,
                                            requestCode = (dateTimeMillis / 1000).toInt()
                                        )
                                    }

//                                    if (reminderId != null) {
//                                        Toast.makeText(context, "Event and reminder added successfully", Toast.LENGTH_SHORT).show()
//                                    }
                                    Toast.makeText(context, "Event created successfully", Toast.LENGTH_SHORT).show()

                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Event not found", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("AddEvent", "Failed to save event and reminder", e)
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        Toast.makeText(context, "Failed to add event.", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                }
            }
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
                singleLine = true,
                onValueChange =
                {
                    eventName = it
                   coroutineScope.launch {
                       validateName(it)
                   }
                },
                    leadingIcon = {
                        Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = "Event Icon"
                    )},
                label = { Text("Event Name", color = MaterialTheme.colorScheme.onTertiary) },
                isError = eventNameError != null,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onTertiary, fontSize = 18.sp, fontWeight = FontWeight.Bold),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    errorBorderColor = Color.Red,
                    cursorColor = MaterialTheme.colorScheme.onTertiary,
                    focusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                    errorLabelColor = Color.Red,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.1f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                )
            )
            eventNameError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
// event description
            OutlinedTextField(
                value = description,
                onValueChange = {  if (it.length <= 250) description = it },
                label = { Text("Description", color = MaterialTheme.colorScheme.onTertiary) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onTertiary, fontSize = 16.sp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Event Description"
                    )},

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    errorBorderColor = Color.Red,
                    cursorColor = MaterialTheme.colorScheme.onTertiary,
                    focusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                    errorLabelColor = Color.Red,
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.1f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                )

            )

//            DatePickerField(
//                label = "Event Date",
//                selectedDate = eventDate,
//                onDateSelected = {
//                    eventDate = it
//                    validateEventDate(it)
//                },
//                errorMessage = eventDateError,
//                boxColor = MaterialTheme.colorScheme.secondary,
//                textColor = MaterialTheme.colorScheme.onTertiary,
//
//            )
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
                onClick = {
                    coroutineScope.launch {
                        validateAndSaveEvent()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )

            ) {
                Text("Save Event")
            }
        }
    }
}