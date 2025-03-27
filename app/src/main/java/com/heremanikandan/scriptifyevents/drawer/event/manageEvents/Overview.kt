package com.heremanikandan.scriptifyevents.drawer.event.manageEvents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.dao.SharedWithDao
import com.heremanikandan.scriptifyevents.utils.convertMillisToDateTime
import com.heremanikandan.scriptifyevents.viewModel.OverviewViewModel
import com.heremanikandan.scriptifyevents.viewModel.factory.OverviewViewModelFactory

@Composable
fun OverviewScreen(eventId: String,
                   eventDao :EventDao,
                   sharedWithDao: SharedWithDao,
                   viewModel: OverviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                       factory = OverviewViewModelFactory(eventId, eventDao, sharedWithDao)
                   )
) {
    val eventDetails by viewModel.eventDetails.collectAsState()
    //val sharedWithList by viewModel.sharedWithList.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary) // Light yellow background
    ) {

        // Event Image Section
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Event Image",
                modifier = Modifier.size(120.dp)
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.onPrimary, shape = CircleShape)
                    .clickable { /* Open Camera */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = androidx.core.R.drawable.ic_call_answer),
                    contentDescription = "Edit Image",
                    tint = Color(0xFF792850)
                )
            }
        }


        eventDetails?.let { event ->

            // Event Title
            Text(
                text = event.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF792850),
                modifier = Modifier.padding(top = 8.dp, start = 16.dp)
            )


            // Event Details Section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = event.description, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))

                val (date, time) = convertMillisToDateTime(event.dateTimeMillis)

                EventDetailRow("Created at :", "24-dec-2024", Icons.Default.CalendarToday)
                EventDetailRow("Date :", date, Icons.Default.Edit)
                EventDetailRow("Created By :", event.createdBy, Icons.Default.Person)
                EventDetailRow("Time :", time, Icons.Default.Schedule)

            }



        }

        // Shared Section
        Text(
            text = "Shared",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF792850),
            modifier = Modifier.padding(16.dp)
        )

        // Bottom Navigation Bar (Custom)

    }
}
// Event Detail Row
@Composable
fun EventDetailRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$label $value", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onTertiary)
        Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.tertiary)
        Spacer(modifier = Modifier.height(22.dp))
    }
}

// Custom Bottom Navigation Item








@Preview(showBackground = true)
@Composable
fun PreviewOverviewScreen() {
   // OverviewScreen("1")
}
