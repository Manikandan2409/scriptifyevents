package com.heremanikandan.scriptifyevents.drawer.event

import android.app.Activity
import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.heremanikandan.scriptifyevents.components.DatePickerField
import com.heremanikandan.scriptifyevents.components.TimePickerField
import com.heremanikandan.scriptifyevents.components.isExactAlarmPermissionGranted
import com.heremanikandan.scriptifyevents.db.ScriptyManager
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.model.Reminder
import com.heremanikandan.scriptifyevents.ui.theme.Yellow60
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager
import com.heremanikandan.scriptifyevents.utils.connections.Permission
import com.heremanikandan.scriptifyevents.utils.connections.SpreadSheet
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
    val viewModel: AddEventViewModel = viewModel(factory = AddEventViewModelFactory(localDbEvent))
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

        if (viewModel.isEventNameExists(name)){
            eventNameError ="Event already Excist"
            return  false
        }

    eventNameError = null
    return true
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
                createdBy = sharedPrefManager.getUserName()!!,
                participants = 0,
                unknown = 0,
                spreadSheetId = ""

            )



            viewModel.insertEvent(newEvent) { isSuccess ->
                Handler(Looper.getMainLooper()).post {
                    if (isSuccess) {
                        val permission = Permission(context)
                        val reminderDateStr = dateFormatter.format(reminderDate)
                        val reminderTimeStr = timeFormatter.format(reminderTime)
                        val reminderTimeMillis = convertToMillis(reminderDateStr, reminderTimeStr)

                        coroutineScope.launch(Dispatchers.IO) {
                            val event = getEventByName(eventName)
                            event?.let {
                                val reminderId = localReminder.insertReminder(
                                    Reminder(eventId = it.id, reminderTimeMillis = reminderTimeMillis)
                                )

                                withContext(Dispatchers.Main) {
                                    val userUid = sharedPrefManager.getUserUid()!!
//                                   to-do ,create a google sheet
//                                      permission.requestPermissionIfMissing(
//                                        userUid = userUid,
//                                        key = "sheets",
//                                        activityResultLauncher = activityResultLauncher,
//                                        onGranted = {
//                                            createAndStoreSpreadSheet(context, viewModel, event)
//                                        },
//                                        onLater = {
//                                            Log.i("ADD_EVENT", "User chose to skip spreadsheet creation.")
//                                        }
//                                    )

                                    if (isExactAlarmPermissionGranted(context)) {
                                        scheduleNotification(context, eventName, "Reminder for $eventName", reminderTimeMillis)
                                        scheduleNotification(context, eventName, "Event starts now", dateTimeMillis)
                                    }

                                    if (reminderId != null) {
                                        showToast(context, "Event and reminder added successfully")
                                    }

                                    navController.popBackStack()
                                }
                            }
                        }
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
                onClick = {
                    coroutineScope.launch {
                        validateAndSaveEvent()
                    }
                },
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




fun createAndStoreSpreadSheet(context: Context,viewModel: AddEventViewModel,event:Event){
    val credential = GoogleAccountCredential.usingOAuth2(
        context,
        listOf(
            "https://www.googleapis.com/auth/spreadsheets",
            "https://www.googleapis.com/auth/drive.file"
        )
    ).apply {
        selectedAccount = GoogleSignIn.getLastSignedInAccount(context)?.account
    }

    val spreadsheetId = SpreadSheet.createSpreadsheet(
        context = context,
        credential = credential,
        sheetTitle = event.name,
        tabTitles = listOf("Attendance", "Participants", "Meta")
    )
    Log.d("ADD EVENT ","SPreadsheet createed with id : $spreadsheetId")
    if (!spreadsheetId.isNullOrEmpty()) {
        viewModel.updateSpreadsheetId(event.id, spreadsheetId)
    }
}




