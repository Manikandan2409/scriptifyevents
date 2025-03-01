@file:OptIn(ExperimentalMaterial3Api::class)

package com.heremanikandan.scriptifyevents.scenes

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.auth.AuthManager
import com.heremanikandan.scriptifyevents.ui.theme.Yellow60
import com.heremanikandan.scriptifyevents.utils.generateOtp
import com.heremanikandan.scriptifyevents.utils.sendOTP
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var emailStatus by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val authManager:AuthManager = AuthManager(context)
    @SuppressLint("SuspiciousIndentation")
    fun generateAndSendOTP(email: String) {
        isLoading = true

        coroutineScope.launch {
            try {
                val otp = generateOtp()
                sendOTP(email, otp,
                    onSuccess = {
                        println("✅ OTP Email sent successfully!")
                        authManager.storeOtpInFirebase(email, otp,
                            onSuccess = {
                                Log.d("Firebase", "OTP stored successfully!")
                                navController.navigate(Screen.OtpVerification.createRoute(email))
                            },
                            onFailure = { exception ->
                                Log.e("Firebase", "Error storing OTP: ${exception.message}")
                            }
                        )
                    },
                    onFailure = { exception ->
                        println("❌ Failed to send OTP: ${exception.message}")
                    }
                ) // Call your existing sendOTP function
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to send OTP", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }


    fun checkEmailExistsAndProceed(inputEmail: String) {
        isLoading = true

        authManager.checkUserExists(inputEmail) { emailExists ->
            isLoading = false
            if (emailExists) {
                emailStatus = "Already exists"
                Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT).show()
            } else {
                emailStatus = "Valid email"
                generateAndSendOTP(inputEmail)
            }
        }
    }

//    fun checkEmailExistsAndProceed(inputEmail: String) {
//        isLoading = true
//        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(inputEmail)
//            .addOnCompleteListener { task ->
//                isLoading = false
//                if (task.isSuccessful) {
//                    val signInMethods = task.result?.signInMethods
//                    if (!signInMethods.isNullOrEmpty()) {
//                        emailStatus = "Already exists"
//                        Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT).show()
//                    } else {
//                        emailStatus = "Valid email"
//                        generateAndSendOTP(inputEmail)
//                    }
//                } else {
//                    emailStatus = "Error checking email"
//                    Toast.makeText(context, "Error checking email", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }



    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Sign Up", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { newEmail ->
                    email = newEmail
                    emailStatus = "" // Reset status while typing
                },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            if (emailStatus.isNotEmpty()) {
                Text(
                    text = emailStatus,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = when (emailStatus) {
                            "Valid email" -> Color.Green
                            "Already exists" -> Color.Red
                            else -> Color.Gray
                        }
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        if (email.isNotEmpty()) {
                            checkEmailExistsAndProceed(email)
                        } else {
                            Toast.makeText(context, "Please enter an email", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Yellow60,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Sign Up")
                }
            }
        }
    }
}
