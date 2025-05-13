package com.heremanikandan.scriptifyevents.screens.menu

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.components.EventCard
import com.heremanikandan.scriptifyevents.db.ScriptyManager
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager
import com.heremanikandan.scriptifyevents.utils.convertMillisToDateTime
import com.heremanikandan.scriptifyevents.viewModel.FilterType
import com.heremanikandan.scriptifyevents.viewModel.HomeViewModel
import com.heremanikandan.scriptifyevents.viewModel.factory.HomeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun HomeScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val localEvents = ScriptyManager.getInstance(context).EventDao()
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(localEvents,context))
    val sharedPrefManager = SharedPrefManager(context)
    val events by viewModel.filteredEvents.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    val selectedEvents = remember { mutableStateListOf<Long>() }
    var isSelectionMode by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        Column {
            // SEARCH & FILTER & SORT BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        viewModel.updateSearchQuery(it.text)
                    },
                    label = { Text("Search Events") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onTertiary) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                        cursorColor = MaterialTheme.colorScheme.onTertiary,
                        focusedBorderColor = MaterialTheme.colorScheme.onTertiary,     // Border color when focused
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,   // Border color when not focused
                        focusedLabelColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

                // SORT DROPDOWN
                Box {
                    Button(onClick = { showSortMenu = true }) {
//                        Text("Sort")
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort",
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                    DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                        DropdownMenuItem(text = { Text("Ascending") }, onClick = {
                            viewModel.sortEvents(ascending = true)
                            showSortMenu = false
                        })
                        DropdownMenuItem(text = { Text("Descending") }, onClick = {
                            viewModel.sortEvents(ascending = false)
                            showSortMenu = false
                        })
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // FILTER DROPDOWN
                Box {
                    Button(onClick = { showFilterMenu = true }) {
//                        Text("Filter")
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onTertiary

                        )
                    }
                    DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                        DropdownMenuItem(text = { Text("All") }, onClick = {
                            viewModel.setFilter(FilterType.ALL)
                            showFilterMenu = false
                        })
                        DropdownMenuItem(text = { Text("Completed") }, onClick = {
                            viewModel.setFilter(FilterType.COMPLETED)
                            showFilterMenu = false
                        })
                        DropdownMenuItem(text = { Text("Ongoing") }, onClick = {
                            viewModel.setFilter(FilterType.ONGOING)
                            showFilterMenu = false
                        })
                        DropdownMenuItem(text = { Text("Waiting") }, onClick = {
                            viewModel.setFilter(FilterType.WAITING)
                            showFilterMenu = false
                        })
                        DropdownMenuItem(text = { Text("Disabled") }, onClick = {
                            viewModel.setFilter(FilterType.DISABLED)
                            showFilterMenu = false
                        })
                    }
                }
            }

            // EVENT LIST
            if (events.isEmpty()) {
                EmptyState()
            } else {

                Box(modifier = Modifier.fillMaxSize()) {
                    Column {
                        if (isSelectionMode) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        selectedEvents.forEach { eventId ->
                                            withContext(Dispatchers.IO) {
                                                val event = viewModel.getEventById(eventId)
                                                event?.let {
                                                    viewModel.deleteEvent(it)
                                                }
                                            }
                                        }
                                        selectedEvents.clear()
                                        isSelectionMode = false
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Selected Events",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        // Your LazyColumn goes here
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(events) { event ->
                                val (date, time) = convertMillisToDateTime(event.dateTimeMillis)
                                EventCard(
                                    id = event.id,
                                    name = event.name,
                                    description = event.description,
                                    createdDate = "2025-02-11",
                                    eventDate = date,
                                    eventTime = time,
                                    createdBy = sharedPrefManager.getUserName() ?: "",
                                    imageRes = R.drawable.ic_launcher_foreground,
                                    isSelected = selectedEvents.contains(event.id),
                                    isSelectionMode = isSelectionMode,
                                    onClick = { eventId ->
                                        if (isSelectionMode) {
                                            if (selectedEvents.contains(eventId)) {
                                                selectedEvents.remove(eventId)
                                            } else {
                                                selectedEvents.add(eventId)
                                            }
                                            if (selectedEvents.isEmpty()) isSelectionMode = false
                                        } else {
                                            navController.navigate("eventDetails/$eventId")
                                        }
                                    },
                                    onLongClick = { eventId ->
                                        isSelectionMode = true
                                        if (!selectedEvents.contains(eventId)) selectedEvents.add(eventId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // FLOATING BUTTON
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddEvent.route) },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
            }
        }


    }
}


@Composable
fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "No Events",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No events found.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}
