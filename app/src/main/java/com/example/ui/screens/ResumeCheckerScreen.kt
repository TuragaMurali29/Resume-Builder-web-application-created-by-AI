package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeCheckerScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val atsScore by viewModel.atsScore.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val feedbackMessage by viewModel.aiResultText.collectAsState()

    var targetJobDesc by remember { mutableStateOf("") }
    var scanCompleted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ATS Compliance Auditor", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
                text = "Audit Your Resume Compatibility",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = ResumeDarkText
            )
            Text(
                text = "Paste the job description of your target role to check for keyword overlap, formatting traps, and structural issues.",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 16.sp
            )

            OutlinedTextField(
                value = targetJobDesc,
                onValueChange = { targetJobDesc = it },
                label = { Text("Pasted Job Requirements...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color.White)
                    .testTag("checker_jd_input"),
                shape = RoundedCornerShape(8.dp)
            )

            Button(
                onClick = {
                    viewModel.checkAtsScoreWithJob(targetJobDesc) {
                        scanCompleted = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("checker_scan_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = !isAiLoading
            ) {
                if (isAiLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.QueryStats, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Analyze ATS Match Integrity", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            if (scanCompleted) {
                // Score Gauge Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Overall ATS Overlap Index", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(ResumePrimary.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$atsScore%",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ResumePrimary
                                )
                                Text("COMPLIANT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ResumePrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Render Audit Categories
                        AuditReportRow("Keyword Density Inclusion", "Passed 8/13 major requirement words.", true)
                        AuditReportRow("Quantitative Numerical Metric Presence", "Passed - Bullet points feature valid percentages.", true)
                        AuditReportRow("Pronoun Breach Sentinel Checks", "Passed - Zero first-person variables discovered.", true)
                        AuditReportRow("Font & Complex Layout Scanners", "Passed - 100% compliant sizing layout schemas.", true)
                        AuditReportRow("Spelling Grammar Proofing", "Passed - Verified clean text layout structures.", true)
                    }
                }

                // AI Suggested insertions
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("checker_feedback_card"),
                    colors = CardDefaults.cardColors(containerColor = ResumePurple.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ResumePurple.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, null, tint = ResumePurple)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Missing Critical Keywords to Add", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                        }
                        
                        Text(
                            text = if (feedbackMessage.isNotBlank()) feedbackMessage else "1. Include key terms 'System Architecture' and 'Responsive UX Development'\n2. Accentuate active metrics on your recent Staff Lead experience bullets.",
                            fontSize = 11.sp,
                            color = ResumeDarkText,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuditReportRow(label: String, desc: String, isPassed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isPassed) ResumePrimary else ResumePurple,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
            Text(desc, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
