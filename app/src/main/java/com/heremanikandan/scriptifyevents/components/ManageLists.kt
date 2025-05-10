package com.heremanikandan.scriptifyevents.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
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




// ⏬ Dropdown Menu for Sort Options
@Composable
fun DropdownMenuComponent(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(
                text = selectedOption ?: "Select Option",
                color = MaterialTheme.colorScheme.onTertiary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                val backgroundColor = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                   MaterialTheme.colorScheme.primary
                }

                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.background(backgroundColor),
                    text = {
                        Text(option, color = MaterialTheme.colorScheme.onTertiary)
                    }
                )

            }
        }
    }
}



