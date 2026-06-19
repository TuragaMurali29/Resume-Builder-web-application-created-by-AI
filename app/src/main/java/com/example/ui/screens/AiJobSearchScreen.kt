package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.JobSearchResult
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiJobSearchScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val results by viewModel.jobSearchResults.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var searchExecuted by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Job Scout & Matcher", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).background(ResumeLightBg)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Discover High-Match Positions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = ResumeDarkText
                )
                Text(
                    text = "Type target keywords. Our AI queries and lists roles with custom match indicators based on your active resume attributes.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 15.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Remote Kotlin Developer") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).background(Color.White).testTag("job_search_input")
                    )

                    Button(
                        onClick = {
                            viewModel.mockJobSearch(searchQuery) {
                                searchExecuted = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("job_search_btn")
                    ) {
                        Icon(Icons.Default.Search, null, tint = Color.White)
                    }
                }

                if (results.isEmpty() && searchExecuted) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No matching roles discovered. Attempt different criteria keys.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(results) { job ->
                            Card(
                                modifier = Modifier.fillMaxWidth().testTag("job_card_${job.id}"),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(0.7f)) {
                                            Text(job.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                                            Text("${job.company} • ${job.location}", fontSize = 11.sp, color = Color.Gray)
                                        }

                                        Surface(
                                            color = ResumePrimary.copy(alpha = 0.12f),
                                            contentColor = ResumePrimary,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "${job.matchScore}% Match",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(job.description, fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
                                    
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.aiTailorResumeToJob(job.description) { tailored ->
                                                    viewModel.updateActiveSummary(tailored)
                                                    notificationMessage = "Successfully rewrote and tailored CV summary to target profile specifications!"
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ResumePurple),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tailor Resume for Role", color = Color.White, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notification banners
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
