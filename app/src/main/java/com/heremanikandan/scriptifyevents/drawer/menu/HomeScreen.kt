package com.heremanikandan.scriptifyevents.drawer.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.utils.EventCard

@Composable
fun HomeScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onPrimary)) {
        Column {
            // Top App Bar
            //MyTopAppBar()

            // Scrollable Event List
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(10.dp)) } // Optional Spacer

                items(14) { num ->
                    EventCard(
                        name = "Sample Event $num",
                        description ="description of the event",
                        createdDate = "2025-02-11",
                        eventDate = "2025-02-20",
                        eventTime = "10:00 AM",
                        createdBy = "admin",
                        imageRes = R.drawable.ic_launcher_foreground // Default image,

                    )
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = {
//                    coroutineScope.launch {
//                        snackbarHostState.showSnackbar("Floating button clicked!")
//                    }
                          navController.navigate(Screen.AddEvent.route)


                },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}