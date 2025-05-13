package com.heremanikandan.scriptifyevents.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EventCard(
    id:Long,
    name: String,
    description:String,
    createdDate: String,
    eventDate: String,
    eventTime: String,
    createdBy:String,
    imageRes: Int,
    onClick :(Long) ->Unit
) {

    val elevated by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(if (elevated) 10.dp else 2.dp)
    val expanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (expanded) 1.2f else 1f)
    Card(

        elevation = CardDefaults.cardElevation(elevation),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .scale(scale)
            .clickable {
                onClick(id)
            }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Image Card
            Card(
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.size(60.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Event Image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Event Details Column
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 18.sp, color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onTertiary)
                Text(text = "$eventDate at $eventTime ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onTertiary)
                Text(text = "$createdDate by $createdBy", fontSize = 13.sp, color = MaterialTheme.colorScheme.onTertiary)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventCard(
    id: Long,
    name: String,
    description: String,
    createdDate: String,
    eventDate: String,
    eventTime: String,
    createdBy: String,
    imageRes: Int,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: (Long) -> Unit,
    onLongClick: (Long) -> Unit
) {
    val elevation by animateDpAsState(if (isSelected) 10.dp else 2.dp)
    val scale by animateFloatAsState(if (isSelected) 1.02f else 1f)

    Card(
        elevation = CardDefaults.cardElevation(elevation),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .scale(scale)
            .combinedClickable(
                onClick = { onClick(id) },
                onLongClick = { onLongClick(id) }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick(id) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Card(
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.size(60.dp)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Event Image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 18.sp, color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onTertiary)
                Text(text = "$eventDate at $eventTime ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onTertiary)
                Text(text = "$createdDate by $createdBy", fontSize = 13.sp, color = MaterialTheme.colorScheme.onTertiary)
            }
        }
    }
}

