package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewPrepScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val prepText by viewModel.interviewPrepText.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var pastedJd by remember { mutableStateOf("") }
    var generationCompleted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interview AI Prep Guide", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ResumeLightBg)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Personalized STAR Prep Drills",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = ResumeDarkText
            )
            Text(
                text = "We read your active resume qualifications and analyze the pasted job requirements to output custom behavioral answers, technical check points and your elevator pitch.",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp
            )

            OutlinedTextField(
                value = pastedJd,
                onValueChange = { pastedJd = it },
                label = { Text("Paste Job Description specs...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color.White)
                    .testTag("prep_jd_input"),
                shape = RoundedCornerShape(8.dp)
            )

            Button(
                onClick = {
                    val finalJd = pastedJd.ifBlank { "Software Engineer specializing in React, TypeScript and high scale state" }
                    // Let's call the ViewModel generator
                    viewModel.generateInterviewPrepGuide(finalJd) {
                        generationCompleted = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("prep_generate_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = !isAiLoading
            ) {
                if (isAiLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Forum, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate Customized STAR Study Guide", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            if (generationCompleted || prepText.isNotBlank()) {
                Text("Your Custom Prep Material", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("prep_output_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (prepText.isNotBlank()) prepText else "1. Top Question: 'Can you describe a complex backend scalability issue you resolved?'\nSTAR Guideline:\n- Situation: High load on backups endpoint.\n- Task: Shrink load from 5s response time.\n- Action: Added moshi compression layers and coroutine thread pools.\n- Result: Improved load to sub 1.2s.\n\n2. Elevator Pitch:\n'Hi, I'm a Senior Systems Engineer backed by 6 years experience coding high optimization databases systems...'",
                            fontSize = 12.sp,
                            color = ResumeDarkText,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
