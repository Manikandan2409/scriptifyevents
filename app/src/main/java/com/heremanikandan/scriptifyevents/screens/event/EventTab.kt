package com.heremanikandan.scriptifyevents.screens.event

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.db.ScriptyManager
import com.heremanikandan.scriptifyevents.db.repos.AttendanceRepository
import com.heremanikandan.scriptifyevents.screens.event.manageEvents.AttendeesScreen
import com.heremanikandan.scriptifyevents.screens.event.manageEvents.OverviewScreen
import com.heremanikandan.scriptifyevents.screens.event.manageEvents.ParticipantsScreen

sealed class EventTab(val route: String, val icon: ImageVector, val label: String) {
    object Overview : EventTab("overview", Icons.Default.Info, "Overview")
    object Attendees : EventTab("attendees", Icons.Default.People, "Attendees")
    object Participants : EventTab("participants", Icons.Default.Person, "Participants")
}
@ExperimentalAnimationApi
@Composable
fun EventScreen(eventId: String,navController: NavController) {
    var selectedTab by remember { mutableStateOf<EventTab>(EventTab.Overview) }
    val context = LocalContext.current
    var db  = ScriptyManager.getInstance(context)
    val eventDao = db.EventDao()
    val sharedWithDao = db.SharedWithDao()
    val participantDao = db.participantDao()
    val attendeesDao = db.attendeesDao()
    val attendanceRepository = AttendanceRepository(attendeesDao,participantDao,db.userDao())
    val id = Integer.parseInt(eventId).toLong()
    var eventName by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(id) {
        val event = eventDao.getEventById(id)
        eventName = event!!.name
    }
    val editable = {
        Toast.makeText(context,"Editable clicked",Toast.LENGTH_SHORT).show()
    navController.navigate(Screen.AddEvent.passEventId(id))
    }
//    val participants = d
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Crossfade(
            targetState = selectedTab,
            animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
            modifier = Modifier.weight(1f)
        ) { tab ->
            when (tab) {
                is EventTab.Overview -> OverviewScreen(id, eventDao, participantDao,attendeesDao,sharedWithDao,editable)
                is EventTab.Attendees -> AttendeesScreen(
                    id,
                    eventName,
                    attendanceRepository,
                    participantDao
                )
                is EventTab.Participants -> ParticipantsScreen(id, participantDao)
            }

        }

            // Bottom Navigation Bar
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            listOf(EventTab.Overview, EventTab.Attendees, EventTab.Participants)
                .forEach { tab ->
                NavigationBarItem(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(if (selectedTab == tab) 32.dp else 24.dp), // Enlarging selected icon
                            tint = if (selectedTab == tab) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary,

                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onTertiary,
                        indicatorColor = Color.Transparent // No background highlight
                    ),
                    label = { Text(tab.label) }
                )
            }
        }

    }
}
