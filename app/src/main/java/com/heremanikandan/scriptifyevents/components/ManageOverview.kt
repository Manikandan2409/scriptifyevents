package com.heremanikandan.scriptifyevents.components

import android.util.Log
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heremanikandan.scriptifyevents.db.model.EventStatus

@Composable
fun AnimatedCountRow(
    participantCount: Long,
    attendanceCount: Long,
    departmentCount: Long
) {
    val cardSize = 80.dp
    val animationDurationMillis = 5000
    val animatedParticipants by animateIntAsState(
        targetValue = participantCount.toInt(),
        animationSpec = tween(durationMillis = animationDurationMillis), label = ""

    )

    val animatedAttendance by animateIntAsState(
        targetValue = attendanceCount.toInt(),
        animationSpec = tween(durationMillis = animationDurationMillis), label = ""
    )

    val animatedDepartments by animateIntAsState(
        targetValue = departmentCount.toInt(),
        animationSpec = tween(durationMillis = animationDurationMillis), label = ""
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
           // .padding( horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {



        StatCard(iconNumber = animatedParticipants, label = "Participants", size = cardSize)
        StatCard(iconNumber = animatedAttendance, label = "Attendance", size = cardSize)
        StatCard(iconNumber = animatedDepartments, label = "Departments", size = cardSize)
    }
}

@Composable
fun StatCard(iconNumber: Int, label: String, size: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =  Modifier.padding(21.dp)

    ) {
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = MaterialTheme.colorScheme.primaryContainer,
                    spotColor = MaterialTheme.colorScheme.primaryContainer
                )
        ) {

            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(0.dp), // Remove default shadow
                modifier = Modifier
                    .fillMaxSize()
//                    .border(
//                        width = 1.dp,
//                        color = MaterialTheme.colorScheme.onTertiary, // Border color
//                        shape = CircleShape
//                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "$iconNumber",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}
//@Composable
//fun EventStatusDropdown(
//    selectedStatus: EventStatus,
//    onStatusSelected: (EventStatus) -> Unit
//) {
//    val TAG = "EVENT STATUS DROP DOWN"
//    var expanded by remember { mutableStateOf(false) }
//    Log.d(TAG,"Event status dropdown called")
//    Box(
//        modifier = Modifier
//            .padding(top = 16.dp)
//            .clip(CircleShape)
//            .background(MaterialTheme.colorScheme.primaryContainer)
//            .clickable { expanded = true }
//            .padding(horizontal = 24.dp, vertical = 12.dp)
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text(
//                text = selectedStatus.name,
//                color = MaterialTheme.colorScheme.onPrimaryContainer,
//                fontWeight = FontWeight.Medium,
//                fontSize = 16.sp
//            )
//            Icon(
//                imageVector = Icons.Default.ArrowDropDown,
//                contentDescription = "Select status",
//                tint = MaterialTheme.colorScheme.onPrimaryContainer
//            )
//        }
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            EventStatus.values().forEach { status ->
//                DropdownMenuItem(
//                    text = { Text(text = status.name) },
//                    onClick = {
//                        onStatusSelected(status)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EventStatusDropdown(
//    selectedStatus: EventStatus,
//    onStatusSelected: (EventStatus) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    val TAG = "EVENT STATUS DROP DOWN"
//    Log.d(TAG,"Event status dropdown called")
//    ExposedDropdownMenuBox(
//        expanded = expanded,
//        onExpandedChange = { expanded = !expanded }
//    ) {
//        TextField(
//            readOnly = true,
//            value = selectedStatus.name,
//            onValueChange = {},
//            label = { Text("Event Status") },
//            trailingIcon = {
//                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//            },
//            modifier = Modifier
//                .menuAnchor()
//                .fillMaxWidth()
//        )
//
//        ExposedDropdownMenu(
//
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//
//        ) {
//            EventStatus.values().forEach { status ->
//                DropdownMenuItem(
//                    text = { Text(status.name) },
//                    onClick = {
//                        onStatusSelected(status)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventStatusDropdown(
    selectedStatus: EventStatus,
    onStatusSelected: (EventStatus) -> Unit,

) {
    var expanded by remember { mutableStateOf(false) }

    // Log for debugging
    Log.d("EVENT STATUS DROP DOWN", "Dropdown Composable called")

    // Custom colors and text styles
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    val textStyle = TextStyle(
        color = contentColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            readOnly = true,
            value = selectedStatus.name,
            onValueChange = {},
            label = { Text("Event Status", color = contentColor) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedLabelColor = contentColor,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSecondary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onPrimary
                )
            ,
            textStyle = textStyle,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(56.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            EventStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.name, style = textStyle) },
                    onClick = {
                        onStatusSelected(status) // ⬅️ Callback to update state & DB
                        expanded = false
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary)

                )
            }
        }
    }
}


