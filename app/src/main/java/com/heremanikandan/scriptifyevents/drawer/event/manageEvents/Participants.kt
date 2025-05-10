package com.heremanikandan.scriptifyevents.drawer.event.manageEvents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.components.FloatingAddButton
import com.heremanikandan.scriptifyevents.components.ParticipantSearchAndSortBar
import com.heremanikandan.scriptifyevents.components.ParticipantsGridView
import com.heremanikandan.scriptifyevents.components.ParticipantsListView
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.db.model.Participant
import com.heremanikandan.scriptifyevents.utils.files.Excel
import com.heremanikandan.scriptifyevents.viewModel.ParticipantViewModel
import com.heremanikandan.scriptifyevents.viewModel.factory.ParticipantViewModelFactory

@Composable
fun ParticipantsScreen(
    eventId: Long,
    participantDao: ParticipantDao
) {
    val viewModel: ParticipantViewModel = viewModel(
        factory = ParticipantViewModelFactory(participantDao, eventId)
    )

    val context = LocalContext.current
    val participants by viewModel.filteredParticipants.collectAsState()
    var isGridView by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleFileResult(context, result, eventId) { participants ->
            participants.forEach { viewModel.addParticipant(it) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp) // Reserve space for FAB to avoid overlap
        ) {
            ParticipantSearchAndSortBar(viewModel, isGridView) {
                isGridView = !isGridView
            }

            if (participants.isEmpty()) {
                NoParticipantsView()
            } else {
                if (isGridView) {
                    ParticipantsGridView(participants, viewModel)
                } else {
                    ParticipantsListView(participants, viewModel)
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Space between buttons
        ) {

            FloatingAddButton(
                onAddParticipant = { name, rollNo, email, course ->
                    val newParticipant = Participant(
                        name = name,
                        rollNo = rollNo,
                        email = email,
                        course = course,
                        eventId = eventId
                    )
                    viewModel.addParticipant(newParticipant)
                },
                filePickerLauncher = { intent ->
                    filePickerLauncher.launch(intent)
                }
            )
        }
    }
}

fun openFilePicker(filePickerLauncher: (Intent) -> Unit) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "*/*"
    val mimeTypes = arrayOf("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    filePickerLauncher(intent)
}

fun handleFileResult(
    context: Context,
    result: ActivityResult,
    eventId: Long,
    onFileRead: (List<Participant>) -> Unit
) {
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.data?.let { uri ->
            val participants = Excel.readParticipantsFromExcel(context, uri, eventId)
            if (!participants.isNullOrEmpty()) {
                onFileRead(participants)
                Toast.makeText(context, "File Imported Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Invalid file format or empty data!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



//// ⬇️ No Participants View
@Composable
fun NoParticipantsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "No Participants"
            )
            Text(text = "No Participants Found", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Log.d("PARTICIPANTS","NO particiapants ")
        }
    }
}


