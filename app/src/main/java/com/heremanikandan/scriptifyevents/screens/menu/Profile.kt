package com.heremanikandan.scriptifyevents.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onPrimary), contentAlignment = Alignment.Center,) {
        Text("Profile Screen", fontSize = 24.sp)
    }
}