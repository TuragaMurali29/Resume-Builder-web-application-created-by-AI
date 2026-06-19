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
fun CoverLetterScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val activeId by viewModel.activeLetterId.collectAsState()
    val title by viewModel.activeLetterTitle.collectAsState()
    val company by viewModel.activeLetterCompany.collectAsState()
    val jobTitle by viewModel.activeLetterJobTitle.collectAsState()
    val body by viewModel.activeLetterBody.collectAsState()
    
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    
    var pastedJobRequirements by remember { mutableStateOf("") }
    var letterTab by remember { mutableStateOf(0) } // 0: Metadata Form, 1: AI Prompt Generator, 2: Final Print Canvas
    var notificationMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title.ifEmpty { "Edit Cover Letter" }, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        notificationMessage = "Processing and downloading cover letter as matching PDF template..."
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Save and Download", tint = ResumePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).background(ResumeLightBg)) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = letterTab, containerColor = Color.White, contentColor = ResumePrimary) {
                    Tab(selected = letterTab == 0, onClick = { letterTab = 0 }, text = { Text("📋 Meta", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                    Tab(selected = letterTab == 1, onClick = { letterTab = 1 }, text = { Text("⚡ AI Generator", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                    Tab(selected = letterTab == 2, onClick = { letterTab = 2 }, text = { Text("📝 Final Canvas", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (letterTab) {
                        0 -> {
                            Text("Employer & Intent Metadata", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ResumeDarkText)
                            Text("Specifying recruiter names and company anchors directly enhances response triggers.", fontSize = 11.sp, color = Color.Gray)
                            
                            OutlinedTextField(
                                value = title,
                                onValueChange = { viewModel.updateLetterTitle(it) },
                                label = { Text("Cover Letter Title Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().background(Color.White)
                            )
                            
                            OutlinedTextField(
                                value = company,
                                onValueChange = { viewModel.updateLetterCompany(it) },
                                label = { Text("Employer / Company Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().background(Color.White).testTag("letter_company_input")
                            )

                            OutlinedTextField(
                                value = jobTitle,
                                onValueChange = { viewModel.updateLetterJobTitle(it) },
                                label = { Text("Target Position Title") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().background(Color.White).testTag("letter_job_title_input")
                            )
                        }

                        1 -> {
                            Text("AI Contextual Cover Writer", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ResumeDarkText)
                            Text("Gemini reads your active experience bullets and synthesizes an incredibly persuasive letter aligning your matching values to the target job description.", fontSize = 11.sp, color = Color.Gray)

                            OutlinedTextField(
                                value = pastedJobRequirements,
                                onValueChange = { pastedJobRequirements = it },
                                label = { Text("Target Job Description Rules herein...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .background(Color.White),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Button(
                                onClick = {
                                    if (company.isBlank() || jobTitle.isBlank()) {
                                        notificationMessage = "Please fulfill Company and Position coordinates in Meta Tab first!"
                                        return@Button
                                    }
                                    viewModel.generateAiCoverLetterDraft(pastedJobRequirements) { draftedText ->
                                        viewModel.updateLetterBody(draftedText)
                                        letterTab = 2
                                        notificationMessage = "Successfully composed Cover Letter Draft!"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("letter_ai_compose_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !isAiLoading
                            ) {
                                if (isAiLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Draft Matching Letter with AI", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        2 -> {
                            Text("Verify Final Draft Content", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ResumeDarkText)
                            
                            OutlinedTextField(
                                value = body,
                                onValueChange = { viewModel.updateLetterBody(it) },
                                label = { Text("Main Body Statement Paragraphs") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .background(Color.White)
                                    .testTag("letter_body_input"),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            // Success notification banners
            AnimatedVisibility(
                visible = notificationMessage.isNotBlank(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp)
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = ResumePrimary)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(notificationMessage, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { notificationMessage = "" }) { Text("Dismiss", color = Color.White, fontSize = 10.sp) }
                    }
                }
            }
        }
    }
}
