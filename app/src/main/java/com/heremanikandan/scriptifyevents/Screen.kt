package com.heremanikandan.scriptifyevents

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Welcome : Screen("welcome_screen")
    object  SignUp :Screen("signup_screen")
    object  Dashboard :Screen("dashboard")
    object OtpVerification : Screen("otp_verification/{email}") { // Accept email parameter
        fun createRoute(email: String) = "otp_verification/$email" // Function to generate route
    }

    object Home : Screen("home")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object AddEvent: Screen("AddEvent")
    object  Logout: Screen("Logout")
}