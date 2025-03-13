package com.heremanikandan.scriptifyevents.drawer.event.manageEvents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ParticipantsScreen(eventId: String) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onPrimary), contentAlignment = Alignment.Center) {
        Text(text = "Participants Screen - Event ID: $eventId", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}