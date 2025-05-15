package com.heremanikandan.scriptifyevents.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.auth.AuthManager
import com.heremanikandan.scriptifyevents.utils.generateOtp
import com.heremanikandan.scriptifyevents.utils.sendOTP
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(email: String, navController: NavController) {
    var otp by remember { mutableStateOf(List(6) { "" }) }
    var activeIndex by remember { mutableStateOf(0) }
    var timer by remember { mutableStateOf(60) }
    var isResendEnabled by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val authManager:AuthManager = AuthManager(context)

    val focusRequesters = List(6) { FocusRequester() }

    fun resendOtp(email: String, callback: (String?, String?) -> Unit) {
            try {
                val otp = generateOtp()
                sendOTP(email, otp,
                    onSuccess = {
                        println("✅ OTP Email sent successfully!")
                        authManager.storeOtpInFirebase(email, otp,
                            onSuccess = {
                                Log.d("Firebase", "OTP stored successfully!")
                            },
                            onFailure = { exception ->
                                Log.e("Firebase", "Error storing OTP: ${exception.message}")
                            }
                        )
                        callback("OTP resent successfully", null)
                    },
                    onFailure = { exception ->
                        println("❌ Failed to send OTP: ${exception.message}")
                        callback(null, "Failed to resend OTP")
                    }
                )
            } catch (e: Exception) {
                callback(null, "Failed to resend OTP: ${e.message}")
            }
        }

    // Timer countdown effect
    LaunchedEffect(timer) {
        while (timer > 0) {
            delay(1000L)
            timer -= 1
        }
        isResendEnabled = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        topBar = {
            TopAppBar(title = { Text("OTP Verification", fontSize = 18.sp) })
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Enter the OTP sent to $email", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                otp.forEachIndexed { index, value ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1) {
                                otp = otp.toMutableList().apply { this[index] = newValue }
                                if (newValue.isNotEmpty() && index < 5) {
                                    activeIndex = index + 1
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .focusRequester(focusRequesters[index])
                            .onKeyEvent {
                                if (it.key == Key.Backspace && index > 0 && otp[index].isEmpty()) {
                                    activeIndex = index - 1
                                    focusRequesters[index - 1].requestFocus()
                                }
                                false
                            }
                            .background(
                                if (index == activeIndex) Color.White else MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.shapes.medium
                            )
                            .border(
                                width = 2.dp,
                                color = if (index == activeIndex) Color.Blue else Color.Gray,
                                shape = MaterialTheme.shapes.medium
                            ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    Log.d("OTP ENTERED","OTP VALUE : $otp")
                    authManager.verifyOtp(email, otp.joinToString("")) { success ->
                        if (success) {

                            navController.navigate(Screen.password.setPassword(email))
                            //authManager.sign
                        } else {
                            // OTP verification failed, handle the error (e.g., show error message)
                            errorMessage = "Invalid OTP or OTP expired."
                        }
                    }
                },
                enabled = otp.all { it.isNotEmpty() }
            ) {
                Text("Verify OTP")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = if (isResendEnabled) "You can resend OTP now" else "Resend OTP in ${timer}s")
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (isResendEnabled) {
                    resendOtp(email) { successMessage, error ->
                        errorMessage = error ?: successMessage
                        timer = 60
                        isResendEnabled = false
                    }
                } }, enabled = isResendEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            {
                Text("Resend OTP")
            }
        }
    }
}


