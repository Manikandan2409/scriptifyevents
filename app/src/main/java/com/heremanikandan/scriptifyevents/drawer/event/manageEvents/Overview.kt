package com.heremanikandan.scriptifyevents.drawer.event.manageEvents

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .size(160.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(18.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Event Image",
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                            }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .padding(18.dp)

                            ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        eventDetails?.let { event ->
                            Text(
                                text = event.name,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

            }
        }

        eventDetails?.let { event ->
                    // Event Details Section
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = event.description.trim().ifEmpty { "No Description available" }, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primaryContainer)
                        Spacer(modifier = Modifier.height(8.dp))
                        val (date, time) = convertMillisToDateTime(event.dateTimeMillis)
                        EventDetailRow("Created at :", "24-dec-2024", false)
                        EventDetailRow("Created By :", event.createdBy, false)
                        EventDetailRow("Date :", date, true)
                        EventDetailRow("Time :", time, true)
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

    }
}

@Composable
fun EventDetailRow(label: String, value: String, editable: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "$label $value",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,

                color = MaterialTheme.colorScheme.onTertiary
            )
            if (editable) {
                Icon(
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}

