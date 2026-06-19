package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AppViewModel,
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var mode by remember { mutableStateOf("login") } // "login", "signup", "forgot"
    
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome to ResumeAI", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ResumeLightBg)
            )
        },
        containerColor = ResumeLightBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = ResumePrimary,
                        modifier = Modifier.size(44.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = when (mode) {
                            "signup" -> "Create Free Account"
                            "forgot" -> "Reset Password"
                            else -> "Log In to ResumeAI"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = ResumeDarkText
                    )

                    Text(
                        text = when (mode) {
                            "signup" -> "Access Enhancv-style resume templates"
                            "forgot" -> "Enter your email to receive recovery instructions"
                            else -> "Pick up right where you left off"
                        },
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    if (feedback.isNotBlank()) {
                        Text(
                            text = feedback,
                            color = if (feedback.startsWith("Success")) ResumePrimary else Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Fields
                    if (mode == "signup") {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("auth_name_input")
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("auth_email_input")
                    )

                    if (mode != "forgot") {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.VpnKey, null) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("auth_pass_input")
                        )
                    }

                    // Main Action button
                    Button(
                        onClick = {
                            if (email.isBlank() || (mode != "forgot" && password.isBlank())) {
                                feedback = "Please satisfy all inputs."
                                return@Button
                            }
                            if (mode == "login") {
                                viewModel.loginSimulated(email, name)
                                onAuthSuccess()
                            } else if (mode == "signup") {
                                viewModel.loginSimulated(email, name)
                                feedback = "Success! Account provisioned."
                                onAuthSuccess()
                            } else {
                                feedback = "Success! Reset link dispatched."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_primary_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (mode) {
                                "signup" -> "Sign Up"
                                "forgot" -> "Send Link"
                                else -> "Sign In"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }

                    // Mock Google OAuth
                    if (mode != "forgot") {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.loginSimulated("julian.vance@techmail.com", "Julian Vance")
                                feedback = "Success! Logged in with Google OAuth."
                                onAuthSuccess()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("auth_google_oauth_btn"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Icon(Icons.Default.AlternateEmail, null, tint = ResumePurple)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continue with Google", color = ResumeDarkText, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mode toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (mode == "login") {
                            TextButton(onClick = { mode = "forgot"; feedback = "" }) {
                                Text("Forgot Password?", fontSize = 11.sp, color = ResumePurple)
                            }
                            TextButton(onClick = { mode = "signup"; feedback = "" }) {
                                Text("Create Account", fontSize = 11.sp, color = ResumePrimary)
                            }
                        } else if (mode == "signup") {
                            TextButton(onClick = { mode = "login"; feedback = "" }) {
                                Text("Already registered? Log In", fontSize = 11.sp, color = ResumePrimary)
                            }
                        } else {
                            TextButton(onClick = { mode = "login"; feedback = "" }) {
                                Text("Back to Login", fontSize = 11.sp, color = ResumePrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
