package com.heremanikandan.scriptifyevents.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.heremanikandan.scriptifyevents.db.dto.AttendanceDTO
import com.heremanikandan.scriptifyevents.utils.convertMillisToDateTime
import com.heremanikandan.scriptifyevents.viewModel.AttendanceViewModel

@Composable
fun AttendeesGrid(attendanceList: List<AttendanceDTO>, searchQuery: String) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(attendanceList.filter { it.name.contains(searchQuery) }) { attendance ->
            AttendanceItem(attendance)
        }
    }
}

@Composable
fun AttendeesList(attendanceList: List<AttendanceDTO>, searchQuery: String) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(attendanceList.filter {
            it.rollNo.contains(searchQuery) || it.name.contains(searchQuery)|| it.course.contains(searchQuery)
        }) {
            AttendanceItem(it)
        }
    }
}

@Composable
fun AttendanceItem(attendance: AttendanceDTO) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Delete Participant",
                modifier = Modifier.size(24.dp)
            )
            Text(text = "Name: ${attendance.name}", style = MaterialTheme.typography.bodyLarge)
            val (date,time)  = convertMillisToDateTime(attendance.dateTimeInMillis)
            Text(text = "Roll No: ${attendance.rollNo} attended at $date:$time ", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FloatingActionButtons(onAddClick: () -> Unit, onQRClick:() -> Unit, onExportClick:  () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End) {

            AnimatedVisibility(visible = expanded) {
                FloatingActionButton(
                    onClick =  onQRClick ,
                    modifier = Modifier
                        .size(42.dp)
                        .offset(y = (-24).dp), // move upwards
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.surface
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR")
                }
            }
            // Left FAB
            AnimatedVisibility(visible = expanded) {
                FloatingActionButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(42.dp)
                        .offset(x = (-56).dp ) // move leftwards
                    ,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.surface
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add ")
                }
            }

            FloatingActionButton(onClick = { expanded=!expanded} ) {
                Icon(Icons.Default.Add, contentDescription = "Add Attendance")
            }
            Spacer(modifier = Modifier.height(8.dp))
            FloatingActionButton(onClick = onExportClick) {
                Icon(Icons.Default.Save, contentDescription = "Export Attendance")
            }
        }
    }
}

@Composable
fun AddAttendanceDialog(onDismiss: () -> Unit, onAddAttendance: (String) -> Unit) {
    val context = LocalContext.current
    val rollNo = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add Attendance") },
        text = {
            Column {
                Text(text = "Enter Roll No:")
                BasicTextField(
                    value = rollNo.value,
                    onValueChange = { rollNo.value = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (rollNo.value.isNotEmpty()) {
                    onAddAttendance(rollNo.value)
                } else {
                    Toast.makeText(context, "Roll No is required!", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}



// Attendance Search bar
@Composable
fun AttendanceSearchAndSortBar(viewModel: AttendanceViewModel, isGridView: Boolean, onLayoutToggle: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Name") }
    var isAscending by remember { mutableStateOf(true) }

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
        DropdownMenuComponent(
            options = listOf("Name", "Roll No"),
            selectedOption = sortOption
        ) {
            sortOption = it
            viewModel.sortAttendance(it, isAscending)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = {
            isAscending = !isAscending
            viewModel.sortAttendance(sortOption, isAscending)
        }) {
            Text(if (isAscending) "Ascending" else "Descending", color = MaterialTheme.colorScheme.onTertiary)
        }
    }
}


