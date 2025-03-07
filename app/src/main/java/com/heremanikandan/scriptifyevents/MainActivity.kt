package com.heremanikandan.scriptifyevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heremanikandan.scriptifyevents.screens.Dashboard
import com.heremanikandan.scriptifyevents.screens.OtpVerificationScreen
import com.heremanikandan.scriptifyevents.screens.SignUpScreen
import com.heremanikandan.scriptifyevents.screens.SplashScreen
import com.heremanikandan.scriptifyevents.screens.WelcomeScreen
import com.heremanikandan.scriptifyevents.ui.theme.ScriptifyeventsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            ScriptifyeventsTheme {
            MyApp()

            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
   // FirebaseApp.initializeApp(context)

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
            Dashboard(navController = navController)
        }
        composable(Screen.OtpVerification.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpVerificationScreen(email = email,navController)
        }

    }
}

