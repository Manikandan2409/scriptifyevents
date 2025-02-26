package com.heremanikandan.scriptifyevents.scenes

import android.annotation.SuppressLint

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.auth.AuthManager
import com.heremanikandan.scriptifyevents.ui.theme.StardosStencil
import com.heremanikandan.scriptifyevents.ui.theme.Yellow60
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



@SuppressLint("RememberReturnType")
@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current


    val authManager = remember { AuthManager(context) }

    /** 🔹 State Variables */
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.TopCenter
    ){
        Image(
            painter = painterResource(id = R.drawable.tl),
            contentDescription = "Top Left Decoration",
            modifier = Modifier
                .size(250.dp)
                // .align(Alignment.TopStart)
                .offset(x = (-50).dp, y = (-20).dp)
        )
        Image(
            painter = painterResource(id = R.drawable.tr),
            contentDescription = "Top Right Decoration",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(160.dp))

            // Heading with Logo

            Text(
                text = "SCRIPTIFY",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = "EVENTS",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green,
                fontFamily = StardosStencil
            )

            Spacer(modifier = Modifier.height(80.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(6.dp, RoundedCornerShape(16.dp)) // Drop shadow
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SIGN IN",
                        fontSize = 20.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 📨 Email Input Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = validateEmail(it)
                        },
                        label = { Text("Email") },
                        isError = emailError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    emailError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔑 Password Input Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = validatePassword(it)
                        },
                        label = { Text("Password") },
                        isError = passwordError != null,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    passwordError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Email Sign-In Button
                    Button(
                        onClick = {
                            if (validateInputs(email, password)) {
                                isLoading = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    val success = authManager.signInWithEmail(email, password)
                                    isLoading = false
                                    if (success) navController.navigate(Screen.Dashboard.route)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        enabled = !isLoading
                    ) {
                        Text(text = "Sign In")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🔹 Google Sign-In Button
                    Button(
                        onClick = {
                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                val success = authManager.signInWithGoogle()
                                isLoading = false
                                if (success) navController.navigate(Screen.Dashboard.route)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        enabled = !isLoading, colors = ButtonDefaults.buttonColors(
                            containerColor = Yellow60,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )


                    ) {
                        Icon(
                            painter = painterResource(id = com.heremanikandan.scriptifyevents.R.drawable.google_icon),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sign in with Google")
                    }
                }
            }
            }
                
    }

}

/** 🔹 Helper Functions for Input Validation */
fun validateEmail(email: String): String? {
    return if (email.isEmpty()) "Email cannot be empty"
    else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Invalid email format"
    else null
}

fun validatePassword(password: String): String? {
    return if (password.isEmpty()) "Password cannot be empty"
    else if (password.length < 6) "Password must be at least 6 characters"
    else null
}

fun validateInputs(email: String, password: String): Boolean {
    return validateEmail(email) == null && validatePassword(password) == null
}
