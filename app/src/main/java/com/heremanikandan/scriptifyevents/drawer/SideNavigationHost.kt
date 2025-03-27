package com.heremanikandan.scriptifyevents.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.drawer.event.AddEvent
import com.heremanikandan.scriptifyevents.drawer.event.EventScreen
import com.heremanikandan.scriptifyevents.drawer.menu.HomeScreen
import com.heremanikandan.scriptifyevents.drawer.menu.ProfileScreen
import com.heremanikandan.scriptifyevents.drawer.menu.SettingsScreen
import com.heremanikandan.scriptifyevents.screens.WelcomeScreen

//
//@Composable
//fun SideBarNavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
//
//
//    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
//        composable(Screen.Home.route) { HomeScreen(navController) }
//        composable(Screen.Settings.route) { SettingsScreen(navController) }
//        composable(Screen.Profile.route) { ProfileScreen(navController) }
//        composable(Screen.AddEvent.route){ AddEvent(navController = navController)}
//
//        composable("eventDetails/{eventId}") { backStackEntry ->
//            val eventId = backStackEntry.arguments?.getString("eventId") ?: "0"
//            EventScreen(eventId)
//        }
//
//
//    }
//}

@Composable
fun SideBarNavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content - Top navigation
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier.weight(1f)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(Screen.AddEvent.route) { AddEvent(navController = navController) }

            composable("eventDetails/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: "0"
                EventScreen(eventId)
            }

            // Add the WelcomeScreen route to navigate after logout
            composable(Screen.Welcome.route) { WelcomeScreen(navController) }
        }

    }
}


