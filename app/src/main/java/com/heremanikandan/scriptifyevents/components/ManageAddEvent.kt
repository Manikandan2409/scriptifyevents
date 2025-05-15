package com.heremanikandan.scriptifyevents.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    errorMessage: String? = null,
    boxColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondary,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onTertiary
) {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Show Date Picker Dialog
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date.format(formatter))
                    }
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = boxColor,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    todayContentColor = MaterialTheme.colorScheme.secondary,
                    weekdayContentColor = textColor,
                    subheadContentColor = textColor
                )
            )
        }
    }

    // Text Field that triggers the picker
    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        label = { Text(text = label, color = textColor) },
        textStyle = TextStyle(fontSize = 16.sp, color = textColor),
        isError = errorMessage != null,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select date",
                tint = textColor
            )
        },
        supportingText = {
            if (errorMessage != null) {
                Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
            }
        },
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
