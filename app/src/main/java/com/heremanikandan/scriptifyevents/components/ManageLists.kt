package com.heremanikandan.scriptifyevents.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heremanikandan.scriptifyevents.db.model.Participant
import com.heremanikandan.scriptifyevents.screens.event.manageEvents.openFilePicker
import com.heremanikandan.scriptifyevents.viewModel.ParticipantViewModel

// 🔍 Search and Sort Bar + Layout Switch
@Composable
fun ParticipantSearchAndSortBar(viewModel: ParticipantViewModel, isGridView: Boolean, onLayoutToggle: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Id") }
    var isAscending by remember { mutableStateOf(true) }
    val sortingOptions = listOf("Id","Name","Roll No")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.updateSearchQuery(it)
            },
            label = { Text("Search") },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        // Toggle between Grid and List View
        IconButton(onClick = onLayoutToggle) {
            Icon(
                imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                contentDescription = "Toggle View"
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))


       sortingOptions.forEach {
           Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                sortOption = it
                viewModel.sortParticipants(sortOption,isAscending)
            },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    containerColor = if (sortOption ==it)  MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
                )
             ) {
                Text(text = it)
            }
           Log.d("Participants","CREAtion of $it completed")
       }

        Spacer(modifier = Modifier.width(16.dp))

        // SORT DROPDOWN
        Box {
            Button(onClick = { isAscending=!isAscending
            viewModel.sortParticipants(sortOption,isAscending)}) {
//                        Text("Sort")
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
                Text(if (isAscending) "Ascending" else "Descending", color = MaterialTheme.colorScheme.primaryContainer)

            }

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
            .background(MaterialTheme.colorScheme.onPrimary)
            .combinedClickable(
                onClick = {},
                onLongClick = { onDelete() }
            )
            .shadow(
                elevation = 1.dp,
                shape = CircleShape,
                ambientColor = MaterialTheme.colorScheme.primary,
                spotColor = MaterialTheme.colorScheme.onSecondary
            )
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Delete Participant",
            tint = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
        ) {
            Text(text = participant.name, fontSize = 18.sp)
        }
    }
}

@Composable
fun FloatingAddButton(
    onAddParticipant: (String, String, String, String) -> Unit,
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



