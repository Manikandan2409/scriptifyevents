package com.heremanikandan.scriptifyevents.drawer.event.manageEvents

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heremanikandan.scriptifyevents.db.model.Attendance
import com.heremanikandan.scriptifyevents.db.model.AttendanceMode
import com.heremanikandan.scriptifyevents.db.repos.AttendanceRepository
import com.heremanikandan.scriptifyevents.viewModel.AttendanceViewModel
import com.heremanikandan.scriptifyevents.viewModel.factory.AttendanceViewModelFactory
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileOutputStream

@Composable
fun AttendeesScreen(eventId: Long, attendanceRepository: AttendanceRepository) {
    val viewModel: AttendanceViewModel = viewModel(
        factory = AttendanceViewModelFactory(attendanceRepository)
    )
    val context = LocalContext.current

    var isGridView by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var attendanceList by remember { mutableStateOf<List<Attendance>>(emptyList()) }

    LaunchedEffect(eventId) {
        viewModel.getAttendanceByEventId(eventId)
    }

    //attendanceList = viewModel.allAttendance.observeAsState(emptyList()).value
    attendanceList = viewModel.allAttendance.observeAsState(emptyList()).value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar and View Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(searchQuery) { query ->
                    searchQuery = query
                }
                IconButton(onClick = { isGridView = !isGridView }) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                        contentDescription = "Toggle View"
                    )
                }
            }

            // List or Grid based on Toggle
            if (isGridView) {
                AttendeesGrid(attendanceList, searchQuery.text)
            } else {
                AttendeesList(attendanceList, searchQuery.text)
            }

            // Floating Action Buttons
            FloatingActionButtons(
                onAddClick = { showDialog = true },
                onExportClick = { exportToExcel(context, attendanceList) }
            )
        }

        if (showDialog) {
            AddAttendanceDialog(
                onDismiss = { showDialog = false },
                onAddAttendance = { rollNo ->
                    val newAttendance = Attendance(
                        eventId = eventId,
                        userId = System.currentTimeMillis(),
                        participantId = rollNo.toLong(),
                        attendantAtInMillis = System.currentTimeMillis(),
                        attendanceMadeBy = AttendanceMode.ENTERED
                    )
                    viewModel.insertAttendance(newAttendance)
                    showDialog = false
                    Toast.makeText(context, "Attendance Added!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun SearchBar(searchQuery: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = "Search")
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AttendeesGrid(attendanceList: List<Attendance>, searchQuery: String) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(attendanceList.filter { it.participantId.toString().contains(searchQuery) }) { attendance ->
            AttendanceItem(attendance)
        }
    }
}

@Composable
fun AttendeesList(attendanceList: List<Attendance>, searchQuery: String) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(attendanceList.filter { it.participantId.toString().contains(searchQuery) }) { attendance ->
            AttendanceItem(attendance)
        }
    }
}

@Composable
fun AttendanceItem(attendance: Attendance) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Roll No: ${attendance.participantId}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Attended At: ${attendance.attendantAtInMillis}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FloatingActionButtons(onAddClick: () -> Unit, onExportClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(horizontalAlignment = Alignment.End) {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Attendance")
            }
            Spacer(modifier = Modifier.height(8.dp))
            FloatingActionButton(onClick = onExportClick) {
                Icon(Icons.Default.List, contentDescription = "Export Attendance")
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

fun exportToExcel(context: android.content.Context, attendanceList: List<Attendance>) {
    val workbook = WorkbookFactory.create(true)
    val sheet = workbook.createSheet("Attendance")

    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("Roll No")
    headerRow.createCell(1).setCellValue("Attended At")

    attendanceList.forEachIndexed { index, attendance ->
        val row = sheet.createRow(index + 1)
        row.createCell(0).setCellValue(attendance.participantId.toString())
        row.createCell(1).setCellValue(attendance.attendantAtInMillis.toString())
    }

    val fileName = "Attendance_${System.currentTimeMillis()}.xlsx"
    val file = File(context.getExternalFilesDir(null), fileName)

    FileOutputStream(file).use { outputStream ->
        workbook.write(outputStream)
        outputStream.close()
    }

    Toast.makeText(context, "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
}
