package com.heremanikandan.scriptifyevents.screens


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.auth.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(email:String,navController: NavController) {
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }
    val authManager = AuthManager(context)
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }

    val allValid = hasUppercase && hasLowercase && hasSpecialChar

    Scaffold(
        containerColor = MaterialTheme.colorScheme.onPrimary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        topBar = {
            TopAppBar(
                title = { Text("Create Password") },

            )
            
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary,
                    focusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onTertiary,
                    cursorColor = MaterialTheme.colorScheme.onTertiary,
                    focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (hasUppercase) "✓ At least one uppercase" else "• At least one uppercase",
                color = if (hasUppercase) Color.Green else Color.Red
            )
            Text(
                text = if (hasLowercase) "✓ At least one lowercase" else "• At least one lowercase",
                color = if (hasLowercase) Color.Green else Color.Red
            )
            Text(
                text = if (hasSpecialChar) "✓ At least one special character" else "• At least one special character",
                color = if (hasSpecialChar) Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val isUserCreated = authManager.signInWithEmail(email, password)
                        withContext(Dispatchers.Main) {
                            if (isUserCreated) {
                                Toast.makeText(context, "Welcome", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(0) { inclusive = true } // Clear entire back stack
                                    launchSingleTop = true
                                }
                            }
                        }
                    }

                },
                enabled = allValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Submit")
            }
        }
    }
}
