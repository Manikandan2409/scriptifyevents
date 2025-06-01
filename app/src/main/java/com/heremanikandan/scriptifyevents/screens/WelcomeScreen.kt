package com.heremanikandan.scriptifyevents.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("Auth", "Additional permissions granted successfully")
            } else {
                Log.e("Auth", "User denied additional permissions")
            }
        }
    val density = LocalDensity.current
    val fontSizeSp = with(density) { 36.dp.toSp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary),
        contentAlignment = Alignment.TopCenter
    ){
        Image(
            painter = painterResource(id = R.drawable.tl),
            contentDescription = "Top Left Decoration",
            modifier = Modifier
                .size(325.dp)
                .offset(x = (-180).dp, y = (-160).dp)
        )
        Image(
            painter = painterResource(id = R.drawable.tr),
            contentDescription = "Top Right Decoration",
            modifier = Modifier
                .size(325.dp)
                .offset(x=(180.dp), y =(-80).dp)
                .align(Alignment.TopEnd)
        )
        Image(
            painter = painterResource(id = R.drawable.lb),
            contentDescription = "Top Right Decoration",
            modifier = Modifier
                .size(325.dp)
                .offset(x=(-210).dp, y =(160).dp)
                .align(Alignment.TopEnd)
        )
        Image(
            painter = painterResource(id = R.drawable.left_top),
            contentDescription = "Top Right Decoration",
            modifier = Modifier
                .size(125.dp)
                .offset(x=(40.dp), y =(650).dp)
                .align(Alignment.TopEnd)
        )
        Image(
            painter = painterResource(id = R.drawable.tr),
            contentDescription = "Top Right Decoration",
            modifier = Modifier
                .size(225.dp)
                .offset(x=((-240).dp), y =(765).dp)
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
                fontSize = fontSizeSp,
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
                fontSize = fontSizeSp,
                fontWeight = FontWeight.Bold,
                color = Color.Green,
                fontFamily = StardosStencil
            )
          //  Spacer(modifier = Modifier.height(80.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp)) // Drop shadow
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
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp).background(color = MaterialTheme.colorScheme.secondary)
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
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp).background(color = MaterialTheme.colorScheme.secondary)
                    )
                    passwordError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // FORGOT PASSWORD
                    Text(
                        text = "Forgot Password?",
                        fontSize = 14.sp,
                        color = Color.Blue,
                        modifier = Modifier.clickable { /* Navigate to Forgot Password */ }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // ✅ Email Sign-In Button
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
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
                        shape = RoundedCornerShape(22.dp),
                        enabled = !isLoading
                    ) {
                        Text(text = "Sign In")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // SIGN UP TEXT
                    Text(
                        text = "Don't have an account?",
                        fontSize = 14.sp,
                        color = Color.Blue,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.SignUp.route)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // 🔹 Google Sign-In Button
                    Button(
                        onClick = {
                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                               val success = authManager.signInWithGoogle(context, activityResultLauncher)
                                withContext(Dispatchers.Main) { // ✅ Switch to Main Thread
                                    isLoading = false
                                    if (success) navController.navigate(Screen.Dashboard.route){
                                        popUpTo(Screen.Welcome.route) { inclusive = true } // Clears backstack
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(24.dp),

                    ) {
                        CompositionLocalProvider(LocalContentColor provides Color.Unspecified) {
                            Icon(
                                painter = painterResource(id = R.drawable.google_icon),
                                contentDescription = "Google Icon",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sign in with Google", color = MaterialTheme.colorScheme.onTertiary )
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
