package com.heremanikandan.scriptifyevents.drawer.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.db.ScriptyManager
import com.heremanikandan.scriptifyevents.utils.EventCard
import com.heremanikandan.scriptifyevents.utils.convertMillisToDateTime
import com.heremanikandan.scriptifyevents.viewModel.HomeViewModel
import com.heremanikandan.scriptifyevents.viewModel.factory.HomeViewModelFactory

@Composable
fun HomeScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val localEvents = ScriptyManager.getInstance(context).EventDao()
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(localEvents))

    val events by viewModel.events.collectAsState()

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)) {
        Column {


            // Scrollable Event List
            if (events.isEmpty()) {
                // Show placeholder when no events exist
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo), // Add your drawable image
                        contentDescription = "No Events",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No events yet. Create one now!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            } else {
                // Show list of events when available
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                    items(events) { event ->
                        val (date, time) = convertMillisToDateTime(event.dateTimeMillis)
                        EventCard(
                            name = event.name,
                            description = event.description,
                            createdDate = "2025-02-11",
                            eventDate = date,
                            eventTime = time,
                            createdBy = event.createdBy,
                            imageRes = R.drawable.ic_launcher_foreground
                        )
                    }
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