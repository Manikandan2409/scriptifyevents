package com.heremanikandan.scriptifyevents.screens.drawer

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.heremanikandan.scriptifyevents.MainActivity
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.auth.AuthManager
import com.heremanikandan.scriptifyevents.screens.menu.ProfileHeader
import com.heremanikandan.scriptifyevents.utils.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(navController: NavHostController, drawerState: DrawerState, coroutineScope: CoroutineScope) {
    val context = LocalContext.current
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
        DrawerMenuItem(title = Screen.Logout.route, icon =Icons.Default.ExitToApp ) {
            performLogout(context,coroutineScope,drawerState)
        }

    }
}

// Function to handle logout logic
fun performLogout(context: Context, coroutineScope: CoroutineScope,
                  drawerState: DrawerState) {

    val manager = SharedPrefManager(context)
    manager.signOut()
    val authManager = AuthManager(context)
    authManager.signOut()
    coroutineScope.launch {
        drawerState.close() // Close drawer after logout
    }

    restartApp(context)
}
// Function to restart the app
fun restartApp(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
    Runtime.getRuntime().exit(0) // Optional: To force kill the current process and start fresh
}