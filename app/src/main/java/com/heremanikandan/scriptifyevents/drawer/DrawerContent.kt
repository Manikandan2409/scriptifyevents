package com.heremanikandan.scriptifyevents.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.drawer.menu.ProfileHeader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(navController: NavHostController, drawerState: DrawerState, coroutineScope: CoroutineScope) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)) {
        // Profile Section
        ProfileHeader()

        // Navigation Items
        DrawerMenuItem(Screen.Home.route, Icons.Default.Home) {
            navController.navigate(Screen.Home.route)
            coroutineScope.launch { drawerState.close() }
        }
        DrawerMenuItem(Screen.Settings.route, Icons.Default.Settings) {
            navController.navigate(Screen.Settings.route)
            coroutineScope.launch { drawerState.close() }
        }
        DrawerMenuItem(Screen.Profile.route, Icons.Default.Person) {
            navController.navigate(Screen.Profile.route)
            coroutineScope.launch { drawerState.close() }
        }
    }
}