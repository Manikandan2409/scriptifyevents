package com.heremanikandan.scriptifyevents

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heremanikandan.scriptifyevents.screens.Dashboard
import com.heremanikandan.scriptifyevents.screens.OtpVerificationScreen
import com.heremanikandan.scriptifyevents.screens.PasswordScreen
import com.heremanikandan.scriptifyevents.screens.SignUpScreen
import com.heremanikandan.scriptifyevents.screens.SplashScreen
import com.heremanikandan.scriptifyevents.screens.WelcomeScreen
import com.heremanikandan.scriptifyevents.ui.theme.ScriptifyeventsTheme
import com.heremanikandan.scriptifyevents.utils.NetworkObserver

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        requestExactAlarmPermission()

        enableEdgeToEdge()
        setContent {
            ScriptifyeventsTheme {
            MyApp()
            }
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (!isGranted) {
                        // Show a message or handle denied permission
                    }
                }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Request Exact Alarm Permission (Android 12+)
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName"))
                startActivity(intent)
            }
        }
    }
}
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val networkObserver = NetworkObserver(context)

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }
        composable(Screen.SignUp.route){
            SignUpScreen(navController = navController)
        }
        composable(Screen.Dashboard.route){
            Dashboard(navController = navController,networkObserver)
        }
        composable(Screen.OtpVerification.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpVerificationScreen(email = email,navController)
        }
        composable(Screen.password.route){
            val email = it.arguments?.getString("email")?:""
            PasswordScreen(email = email, navController = navController)
        }
    }
}

