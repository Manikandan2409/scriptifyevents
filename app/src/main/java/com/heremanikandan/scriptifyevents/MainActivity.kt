package com.heremanikandan.scriptifyevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heremanikandan.scriptifyevents.scenes.SignUpScreen
import com.heremanikandan.scriptifyevents.scenes.SplashScreen
import com.heremanikandan.scriptifyevents.scenes.WelcomeScreen
import com.heremanikandan.scriptifyevents.ui.theme.ScriptifyeventsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}