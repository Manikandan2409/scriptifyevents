package com.heremanikandan.scriptifyevents.components

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TypeSpecimen
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.heremanikandan.scriptifyevents.db.dto.AttendanceDTO
import com.heremanikandan.scriptifyevents.utils.convertMillisToDateTime
import com.heremanikandan.scriptifyevents.viewModel.AttendanceViewModel
import kotlinx.coroutines.delay

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

/**
 * Attendance unit
 */
@Composable
fun AttendanceItem(attendance: AttendanceDTO) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                ambientColor = MaterialTheme.colorScheme.onPrimary,
                spotColor = MaterialTheme.colorScheme.primary
            )
        ,
        elevation = CardDefaults.cardElevation(4.dp),
        colors =  CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )

    ) {
        Column(modifier = Modifier
            .padding(16.dp)
        )
        {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Delete Participant",
                tint = MaterialTheme.colorScheme.primaryContainer
            )
            Text(text = "Name: ${attendance.name}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onTertiary)
            val (date,time)  = convertMillisToDateTime(attendance.dateTimeInMillis)
            Text(text = "Roll No: ${attendance.rollNo} attended at $date:$time ", style = MaterialTheme.typography.bodyMedium, color =  MaterialTheme.colorScheme.onTertiary)
        }
    }
}

/**
  Attendance Manage buttons
 */
@Composable
fun AttendanceManageActionButtons(onAddClick: () -> Unit, onQRClick:() -> Unit, onExportClick:  () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column( modifier = Modifier
            .align(Alignment.BottomEnd),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

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
                        .offset(y = (-12).dp) // move leftwards
                    ,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.surface
                ) {
                    Icon(Icons.Default.TypeSpecimen, contentDescription = "Add ")
                }
            }
            FloatingActionButton(onClick = { expanded=!expanded} ) {
                Icon(Icons.Default.Add, contentDescription = "Add Attendance")
            }
          //  Spacer(modifier = Modifier.height(8.dp))
            FloatingActionButton(onClick = onExportClick) {
                Icon(Icons.Default.Save, contentDescription = "Export Attendance")
            }
        }
    }
}

/**
 *  Manual attendance Dialog
 **/
@Composable
fun AddAttendanceDialog(
    onDismiss: () -> Unit,
    onAddAttendance: (String) -> Unit
) {
    val context = LocalContext.current
    val rollNo = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.onPrimary // Whole dialog background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = "Add Attendance",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                ) {

                    Text(text = "Enter Roll No:")
                    Spacer(modifier = Modifier.height(24.dp))

                    BasicTextField(
                        value = rollNo.value,
                        onValueChange = { rollNo.value = it.uppercase() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                            .focusRequester(focusRequester),
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            if (rollNo.value.isNotEmpty()) {
                                onAddAttendance(rollNo.value)
                            } else {
                                Toast.makeText(context, "Roll No is required!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }

    // Show keyboard and request focus
    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

/**
 *  Attendance Search bar
**/
@Composable
fun AttendanceSearchAndSortBar(viewModel: AttendanceViewModel, isGridView: Boolean, onLayoutToggle: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Id") }
    var isAscending by remember { mutableStateOf(true) }
    val sortingOptions = listOf("Id","Name","Roll No")
    val isListButtonVisible by remember { mutableStateOf(false) }
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
            label = { Text("Search Attendees ...") },
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
        // Toggle between Grid and List View
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
                    tint = MaterialTheme.colorScheme.primaryContainer,
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
                    viewModel.sortAttendance(sortOption,isAscending)
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    containerColor = if (sortOption ==it)  MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = it)
            }
            Log.d("ATTENDEES","CREAtion of $it completed")
        }
        Spacer(modifier = Modifier.width(16.dp))

        Box {
            Button(
                onClick = { isAscending=!isAscending
                viewModel.sortAttendance(sortOption,isAscending)},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Icon(
                    imageVector = if (isAscending)Icons.Default.ArrowDownward else Icons.Default.ArrowUpward ,
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
                Text(if (isAscending) "A-Z" else "Z-A", )

            }

        }
    }
}


