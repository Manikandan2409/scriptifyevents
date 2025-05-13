package com.heremanikandan.scriptifyevents.screens.event.manageEvents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.zxing.integration.android.IntentIntegrator
import com.heremanikandan.scriptifyevents.components.AddAttendanceDialog
import com.heremanikandan.scriptifyevents.components.AttendanceSearchAndSortBar
import com.heremanikandan.scriptifyevents.components.AttendeesGrid
import com.heremanikandan.scriptifyevents.components.AttendeesList
import com.heremanikandan.scriptifyevents.components.AttendanceManageActionButtons
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.db.model.Attendance
import com.heremanikandan.scriptifyevents.db.model.AttendanceMode
import com.heremanikandan.scriptifyevents.db.repos.AttendanceRepository
import com.heremanikandan.scriptifyevents.utils.CaptureActivityPortrait
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager
import com.heremanikandan.scriptifyevents.utils.files.Excel
import com.heremanikandan.scriptifyevents.viewModel.AttendanceViewModel
import com.heremanikandan.scriptifyevents.viewModel.factory.AttendanceViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun AttendeesScreen(eventId: Long,eventName:String?, attendanceRepository: AttendanceRepository,participantDao: ParticipantDao) {
    val viewModel: AttendanceViewModel = viewModel(
        factory = AttendanceViewModelFactory(attendanceRepository,eventId,participantDao)

    )
    val context = LocalContext.current
    var isGridView by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val attendanceList by  viewModel.filteredAttendence.collectAsState()
    val sharedPrefManager = SharedPrefManager(context)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(eventId) {
        viewModel.getAttendanceByEventId(eventId)
    }
    var scannedText by remember { mutableStateOf("Not scanned yet") }

    suspend fun addAttendance(pid:Long ){
        val newAttendance = Attendance(
            eventId = eventId,
            userId = sharedPrefManager.getUserUid()!!.toString(),
            participantId = pid, // use `pid`, not rollNo.toLong()
            attendantAtInMillis = System.currentTimeMillis(),
            attendanceMadeBy = AttendanceMode.ENTERED
        )
        Log.d("INSERT ATTENDANCE","new Attendance $newAttendance")
        viewModel.insertAttendance(newAttendance)
    }

    fun callQRScanner(
        context: Context,
        launcher: ActivityResultLauncher<Intent>
    ) {
        val integrator = IntentIntegrator(context as Activity)
        integrator.setPrompt("Scan a QR code or barcode")
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(true)
        integrator.captureActivity = CaptureActivityPortrait::class.java
        launcher.launch(integrator.createScanIntent())
    }

    var showRetryDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val contents = IntentIntegrator.parseActivityResult(result.resultCode, intent)?.contents
            if (contents != null) {
                scannedText = contents

                coroutineScope.launch {
                    val pid = viewModel.getParticipantID(scannedText)

                    if (pid == null) {
                        showRetryDialog = true // Ask user
                        return@launch
                    }
                    addAttendance(pid)
                }
            } else {
                Toast.makeText(context, "Cannot read attendance", Toast.LENGTH_SHORT).show()
                scannedText = "Cancelled or failed"
            }
        }
    }

    if (showRetryDialog) {
        AlertDialog(
            onDismissRequest = { showRetryDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showRetryDialog = false
                    callQRScanner(context, launcher)
                }) {
                    Text("Retry")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRetryDialog = false
                Toast.makeText(context,"Cannot add attendance", Toast.LENGTH_SHORT).show()}) {
                    Text("Cancel")
                }
            },
            title = { Text("Participant not found") },
            text = { Text("Do you want to scan again?") }
        )
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        onResult = { uri ->
            if (uri != null && eventName != null) {
                Excel.exportToExcel(context, eventName, attendanceList, uri)
            } else {
                Toast.makeText(context, "File not saved. Missing data", Toast.LENGTH_SHORT).show()
            }
        }
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar and View Toggle


            AttendanceSearchAndSortBar(viewModel = viewModel, isGridView = isGridView) {
                isGridView = !isGridView
            }
            if (attendanceList.isEmpty()){
                NoParticipantsView()
            }else{

                // List or Grid based on Toggle
                if (isGridView) {
                    AttendeesGrid(attendanceList, searchQuery.text)
                } else {
                    AttendeesList(attendanceList, searchQuery.text)
                }
            }

        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Space between buttons
        ) {

          //   Floating Action Buttons
            AttendanceManageActionButtons(
                onAddClick = { showDialog = true },
                onQRClick =  {
//                    val integrator = IntentIntegrator(context as Activity)
//                    integrator.setPrompt("Scan a QR code or barcode")
//                    integrator.setBeepEnabled(true)
//                    integrator.setOrientationLocked(true)
//                    integrator.captureActivity = CaptureActivityPortrait::class.java
//                    launcher.launch(integrator.createScanIntent())
                             callQRScanner(context,launcher)
                },
                onExportClick = {
                    val fileName = "Attendance_${eventName ?: "Event"}_${System.currentTimeMillis()}.xlsx"
                    createDocumentLauncher.launch(fileName)
                }
            )

        }

        if (showDialog) {
            AddAttendanceDialog(
                onDismiss = { showDialog = false },
                onAddAttendance = { rollNo ->
                    coroutineScope.launch {
                        val pid = viewModel.getParticipantID(rollNo)
                        if (pid!= null){
                            addAttendance(pid)
                            showDialog = false
                            Toast.makeText(context, "$rollNo Attendance Added!", Toast.LENGTH_SHORT).show()
                            return@launch
                        }else{
                            Toast.makeText(context, "$rollNo NO Valid User", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            )
        }
    }
}




