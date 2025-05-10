package com.heremanikandan.scriptifyevents.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedCountRow(
    participantCount: Long,
    attendanceCount: Long,
    departmentCount: Long
) {
    val cardSize = 80.dp
    val animationDurationMillis = 5000
    val animatedParticipants by animateIntAsState(
        targetValue = participantCount.toInt(),
        animationSpec = tween(durationMillis = animationDurationMillis)
    )

    val animatedAttendance by animateIntAsState(
        targetValue = attendanceCount.toInt(),
        animationSpec = tween(durationMillis = animationDurationMillis)
    )

    val animatedDepartments by animateIntAsState(
        targetValue = departmentCount.toInt(),
        animationSpec = tween(durationMillis = animationDurationMillis)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
           // .padding( horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {



        StatCard(iconNumber = animatedParticipants, label = "Participants", size = cardSize)
        StatCard(iconNumber = animatedAttendance, label = "Attendance", size = cardSize)
        StatCard(iconNumber = animatedDepartments, label = "Departments", size = cardSize)
    }
}

@Composable
fun StatCard(iconNumber: Int, label: String, size: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =  Modifier.padding(21.dp)

    ) {
        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = MaterialTheme.colorScheme.primaryContainer,
                    spotColor = MaterialTheme.colorScheme.primaryContainer
                )
        ) {

            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(0.dp), // Remove default shadow
                modifier = Modifier
                    .fillMaxSize()
//                    .border(
//                        width = 1.dp,
//                        color = MaterialTheme.colorScheme.onTertiary, // Border color
//                        shape = CircleShape
//                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "$iconNumber",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}
