package com.heremanikandan.scriptifyevents.components

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    var isListButtonVisible by remember { mutableStateOf(false) }

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
            label = { Text("Search Pariticipants ...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onTertiary) },
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                cursorColor = MaterialTheme.colorScheme.onTertiary,
                focusedBorderColor = MaterialTheme.colorScheme.onTertiary,     // Border color when focused
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,   // Border color when not focused
                focusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
            )
        )

       if (isListButtonVisible){
           Button(
               onClick = onLayoutToggle,
               colors = ButtonDefaults.buttonColors(
                   contentColor = MaterialTheme.colorScheme.onTertiary,
                   containerColor = MaterialTheme.colorScheme.primary
               )
           )
           {
               Icon(
                   imageVector = if (isGridView) Icons.Outlined.Checklist else Icons.Default.GridView,
                   contentDescription = "Toggle View",
                   tint = MaterialTheme.colorScheme.primaryContainer
               )
           }
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
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                onClick = { isAscending=!isAscending
            viewModel.sortParticipants(sortOption,isAscending)}) {
                Icon(
                    imageVector = if (isAscending)Icons.Default.ArrowDownward else Icons.Default.ArrowUpward ,
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
                Text(if (isAscending) "A-Z" else "Z-A", color = MaterialTheme.colorScheme.onTertiary)

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
    val dynamicColor = remember(participant.name) {
        // Create a color dynamically (for example, using hashCode)
        Color((participant.name.hashCode() * 0xFFFFFF).toInt() or 0xFF000000.toInt())
    }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.onPrimary)
            .combinedClickable(
                onClick = {},
                onLongClick = { onDelete() }
            )
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = participant.name.trim().firstOrNull()?.uppercase() ?: "?",
                    color = dynamicColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily =  FontFamily.Default
                )
            }
            Text(text = participant.name.trim().uppercase(), fontSize = 16.sp)
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
                ParticipantListItem(participant = participant, onDelete = {viewModel.deleteParticipant(participant)}, onEdit = {participant ->viewModel.updateParticipant(participant)  })
            }
        }
    }


@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("RememberReturnType")
@Composable
fun ParticipantListItem(
    participant: Participant,
    onDelete: () -> Unit,
    onEdit: (Participant) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember(participant.id) { mutableStateOf(participant.name) }
    var editedEmail by remember(participant.id) { mutableStateOf(participant.email) }
    var editedCourse by remember(participant.id) { mutableStateOf(participant.course) }
    val dynamicColor = remember(participant.name) {
        Color((participant.name.hashCode() * 0xFFFFFF) or 0xFF000000.toInt())
    }
    val backgroundColor = if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    else MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .combinedClickable(
                onClick = { isExpanded = !isExpanded },
                onLongClick = { onDelete() }
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Dynamic Avatar Box
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = participant.name.firstOrNull()?.uppercase() ?: "?",
                        color = dynamicColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily =  FontFamily.Default
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = participant.rollNo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                    Text(
                        text = participant.name,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }

            // Dropdown Icon
            Icon(
                imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { isExpanded = !isExpanded }
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            if (isEditing) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Column(modifier = Modifier.padding(22.dp).weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            EditableField(
                                label = "Name",
                                value = editedName,
                                icon = Icons.Default.Person
                            ) { newValue -> editedName = newValue }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            EditableField(
                                label = "Email",
                                value = editedEmail,
                                icon = Icons.Outlined.Email
                            ) { newValue -> editedEmail = newValue }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            EditableField(
                                label = "Course",
                                value = editedCourse,
                                icon = Icons.Outlined.Bookmark
                            ) { newValue -> editedCourse = newValue }
                        }
                        Button(onClick = {
                            val updatedParticipant = participant.copy(
                                name = editedName,
                                email = editedEmail,
                                course = editedCourse
                            )
                            onEdit(updatedParticipant)
                            isEditing = false
                        }) {
                            Text("Save")
                        }
                    }
                }
            }
            else{
                Row(modifier = Modifier.padding(12.dp)) {
                    Column(modifier = Modifier.padding(top = 22.dp).weight(1f)) {
                        participant.email?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = "Email",
                                    tint = MaterialTheme.colorScheme.primaryContainer  // Customize color here
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Email: $it",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp)) // Add spacing between rows if needed
                        }

                        participant.course?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp)
                            ) {
                                Icon(
                                    Icons.Default.BookmarkBorder,
                                    contentDescription = "Course",
                                    tint = MaterialTheme.colorScheme.primaryContainer  // Customize color here
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Course: $it",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                    }
                    Column(modifier = Modifier.padding(12.dp).align(Alignment.CenterVertically)) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                        IconButton(onClick = { onDelete()
                        isEditing=false
                        }) {
                            Icon(

                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red

                            )
                        }
                    }
                }
            }

        }
    }



}


@Composable
fun EditableField(label:String, value:String, icon:ImageVector,  onValueChange: (String) -> Unit){
    OutlinedTextField(
        value = value,
        singleLine = true,
        onValueChange = {
                        onValueChange(it)
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "Event Icon"
            )},
        label = { Text(label, color = MaterialTheme.colorScheme.onTertiary) },
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



