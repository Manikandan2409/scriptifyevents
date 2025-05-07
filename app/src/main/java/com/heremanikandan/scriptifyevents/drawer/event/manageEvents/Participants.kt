package com.heremanikandan.scriptifyevents.drawer.event.manageEvents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.components.ParticipantSearchAndSortBar
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
                context = context,
                filePickerLauncher = { intent ->
                    filePickerLauncher.launch(intent)
                }
            )
        }
    }
}

@Composable
fun ParticipantsGridView(participants: List<Participant>, viewModel: ParticipantViewModel) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        items(participants.size) { index ->
            val participant = participants[index]
            ParticipantGridItem(participant) {
                viewModel.deleteParticipant(participant)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParticipantGridItem(participant: Participant, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .combinedClickable(
                onClick = {},
                onLongClick = { onDelete() }
            )
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Delete Participant",
                modifier = Modifier.size(24.dp)
            )
            Text(text = participant.name, fontSize = 16.sp)
        }
    }
}
@Composable
fun ParticipantsListView(participants: List<Participant>, viewModel: ParticipantViewModel) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)) {
        items(participants.size) { index ->
            val participant = participants[index]
            ParticipantListItem(participant) {
                viewModel.deleteParticipant(participant)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParticipantListItem(participant: Participant, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
            //.clickable(onLongClick = onDelete) {}
            .combinedClickable(
                onClick = {},
                onLongClick = { onDelete() }
            )
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Delete Participant",
            tint = Color.Red,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = participant.name, fontSize = 18.sp)
        }
    }
}

@Composable
fun FloatingAddButton(
    onAddParticipant: (String, String, String, String) -> Unit,
    context: Context,
    filePickerLauncher: (Intent) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) } // Remember to manage dialog state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Upload File Button
            FloatingActionButton(
                onClick = {
                    openFilePicker { intent ->
                        filePickerLauncher.invoke(intent)
                    }
                }
            ) {
                Icon(Icons.Default.Upload, contentDescription = "Upload Docs")
            }

            // Add Participant Button
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Participant")
            }
        }
    }

    // Add Participant Dialog
    if (showDialog) {
        AddParticipantDialog(
            onDismiss = { showDialog = false },
            onAdd = { name, rollNo, email, course ->
                onAddParticipant(name, rollNo, email, course)
                showDialog = false
            }
        )
    }
}


@Composable
fun AddParticipantDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Participant") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = rollNo, onValueChange = { rollNo = it }, label = { Text("Roll No") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = course, onValueChange = { course = it }, label = { Text("Course") })
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(name, rollNo, email, course) }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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


