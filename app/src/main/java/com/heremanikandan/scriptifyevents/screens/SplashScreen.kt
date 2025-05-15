package com.heremanikandan.scriptifyevents.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.sharedPref.SharedPrefManager
import kotlinx.coroutines.delay

@SuppressLint("SuspiciousIndentation")
@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
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
      val shared = SharedPrefManager(context)
        Log.d("SPLASH SCREEN AUTH ","is user logged in : ${shared.isUserLoggedIn()}")
        if (shared.isUserLoggedIn()) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }else{

            navController.navigate(Screen.Welcome.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
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
