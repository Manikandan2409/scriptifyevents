package com.heremanikandan.scriptifyevents.scenes

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 1f,
        animationSpec = tween(durationMillis = 2000, easing = EaseOutQuad),
        label = "scaleAnimation"
    )


    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 3000, easing = EaseOutQuad),
        label = "alphaAnimation"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000) // Wait for the animation to complete
        navController.navigate(Screen.Welcome.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary), // Set background color
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Ensure the image is in drawable
            contentDescription = "Scriptify Events Logo",
            modifier = Modifier
                .size(150.dp) // Adjust size as needed
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    alpha = alpha
                )
        )
    }
}
