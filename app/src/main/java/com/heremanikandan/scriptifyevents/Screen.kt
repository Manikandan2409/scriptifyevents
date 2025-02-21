package com.heremanikandan.scriptifyevents

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Welcome : Screen("welcome_screen")
    object  SignUp :Screen("signup_screen")
}