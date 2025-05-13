package com.heremanikandan.scriptifyevents.screens.drawer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.screens.event.AddEvent
import com.heremanikandan.scriptifyevents.screens.event.EventScreen
import com.heremanikandan.scriptifyevents.screens.menu.HomeScreen
import com.heremanikandan.scriptifyevents.screens.menu.ProfileScreen
import com.heremanikandan.scriptifyevents.screens.menu.SettingsScreen
import com.heremanikandan.scriptifyevents.screens.WelcomeScreen


@OptIn(ExperimentalAnimationApi::class)
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
         //   composable(Screen.AddEvent.route) { AddEvent(navController = navController) }
            composable(
                route = "AddEvent?eventId={eventId}",
                arguments = listOf(
                    navArgument("eventId") {
                        type = NavType.LongType
                        defaultValue = Long.MIN_VALUE
                    }
                )
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getLong("eventId") ?: Long.MIN_VALUE
                AddEvent(navController = navController, eventId = eventId)
            }

            composable("eventDetails/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: "0"
                EventScreen(eventId,navController)
            }

            // Add the WelcomeScreen route to navigate after logout
            composable(Screen.Welcome.route) { WelcomeScreen(navController) }
        }

    }
}


