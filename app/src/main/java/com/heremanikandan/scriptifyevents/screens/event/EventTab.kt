
package com.heremanikandan.scriptifyevents.screens.event

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch

sealed class EventTab(val icon: ImageVector, val label: String) {
    object Overview : EventTab(Icons.Default.Info, "Overview")
    object Attendees : EventTab(Icons.Default.People, "Attendees")
    object Participants : EventTab(Icons.Default.Person, "Participants")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EventScreen(eventId: String, navController: NavController) {
    val context = LocalContext.current
    val db = ScriptyManager.getInstance(context)
    val eventDao = db.EventDao()
    val sharedWithDao = db.SharedWithDao()
    val participantDao = db.participantDao()
    val attendeesDao = db.attendeesDao()
    val attendanceRepository = AttendanceRepository(attendeesDao, participantDao, db.userDao())
    val id = eventId.toLong()
    var eventName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        val event = eventDao.getEventById(id)
        eventName = event?.name
    }

    val editable = {
        Toast.makeText(context, "Editable clicked", Toast.LENGTH_SHORT).show()
        navController.navigate(Screen.AddEvent.passEventId(id))
    }

    val tabs = listOf(EventTab.Overview, EventTab.Attendees, EventTab.Participants)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Pager for swiping
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            Crossfade(
                targetState = page,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            ) { tabIndex ->
                when (tabs[tabIndex]) {
                    is EventTab.Overview -> OverviewScreen(id, eventDao, participantDao, attendeesDao, sharedWithDao, editable)
                    is EventTab.Attendees -> AttendeesScreen(id, eventName, attendanceRepository, participantDao)
                    is EventTab.Participants -> ParticipantsScreen(id, participantDao)
                }
            }
        }

        // Tab Row with clickable icons and labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                Column(

                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(if (pagerState.currentPage == index) 32.dp else 24.dp),
                        tint = if (pagerState.currentPage == index) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiary
                    )
                    Text(
                        text = tab.label,
                        color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}
