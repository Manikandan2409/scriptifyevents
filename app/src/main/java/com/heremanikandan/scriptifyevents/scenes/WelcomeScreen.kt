package com.heremanikandan.scriptifyevents.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heremanikandan.scriptifyevents.R
import com.heremanikandan.scriptifyevents.Screen
import com.heremanikandan.scriptifyevents.ui.theme.StardosStencil
import com.heremanikandan.scriptifyevents.ui.theme.Yellow60
import com.heremanikandan.scriptifyevents.viewModel.LoginViewModel

@Composable
fun WelcomeScreen(navController: NavController,viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.TopCenter
    ) {
        // Decorative Assets at Edges
        Image(
            painter = painterResource(id = R.drawable.tl),
            contentDescription = "Top Left Decoration",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopStart)
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

                    // EMAIL TEXTFIELD
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = { Text("Email") },
                        isError = uiState.emailError.isNotEmpty(),
                        shape = RoundedCornerShape(15.dp), // Rounded corners
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (uiState.emailError.isNotEmpty()) {
                        Text(
                            text = uiState.emailError,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // PASSWORD TEXTFIELD
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        label = { Text("Password") },
                        isError = uiState.passwordError.isNotEmpty(),
                        shape = RoundedCornerShape(15.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (uiState.passwordError.isNotEmpty()) {
                        Text(
                            text = uiState.passwordError,
                            color = Color.Red,
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

                    // SUBMIT BUTTON
                    Button(
                        onClick = {
                            viewModel.onSubmit()
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
                        Text(text = "Submit", fontWeight = FontWeight.Bold)

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

                    // GOOGLE SIGN-IN BUTTON
                    Button(
                        onClick = { /* Google Sign-In */ },
                        colors = ButtonDefaults.buttonColors(containerColor =MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onTertiary)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.google_icon),
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
