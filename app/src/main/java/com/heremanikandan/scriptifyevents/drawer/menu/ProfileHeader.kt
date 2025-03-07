package com.heremanikandan.scriptifyevents.drawer.menu

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.Coil
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager

@Composable
fun ProfileHeader() {
   val context = LocalContext.current
    val sharedManger = SharedPrefManager(context)
    Coil.imageLoader(context).memoryCache?.clear()

    val photoUri: Uri? = sharedManger.getPhotoUri(context)
    Log.d("ProfileImage", "Image URI: $photoUri")

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(photoUri)
            .placeholder(R.drawable.profile) // Show default while loading
            .error(R.drawable.profile) // Show default if loading fails
            .build()
    )

    val painterState = painter.state

    LaunchedEffect(painterState) {
        when (painterState) {
            is AsyncImagePainter.State.Loading -> Log.d("ProfileImage", "Image is loading...")
            is AsyncImagePainter.State.Success -> Log.d("ProfileImage", "Image loaded successfully!")
            is AsyncImagePainter.State.Error -> Log.e("ProfileImage", "Image failed to load!")
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //sharedManger.getPhotoUri(context)
        Image(
           // painter = painterResource(id = R.drawable.profile), // Your profile image
            painter = painter,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(sharedManger.getUserName()?:"default user", color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold)
        Text(sharedManger.getUserEmail()?:"default@gmail.com", color = MaterialTheme.colorScheme.onTertiary, fontSize = 12.sp)
    }
}