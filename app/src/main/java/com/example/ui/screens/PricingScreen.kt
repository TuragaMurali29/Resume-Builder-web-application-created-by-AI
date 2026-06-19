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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val userPlan by viewModel.userPlan.collectAsState()
    var successUpgradeMsg by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium Pricing Plans", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
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
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = ResumePurple,
                    modifier = Modifier.size(54.dp)
                )

                Text(
                    text = "Unlock Your Executive Potential",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ResumeDarkText,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Land more offers, bypass layout parsing traps, and automate cover letter outlines.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Current Plan Card Displays
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, ResumePrimary)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CURRENT LEVEL STATUS: " + userPlan.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = ResumePrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (userPlan == "Pro") "$19.99 / month" else "$0.00 / free-tier",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = ResumeDarkText
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.togglePlan()
                                successUpgradeMsg = if (userPlan == "Free") {
                                    "Upgrade complete! Welcome to ResumeAI PRO."
                                } else {
                                    "Downgraded to free-tier status."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("pricing_toggle_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (userPlan == "Free") "Upgrade to PRO (Dynamic Sandbox Sim)" else "Revert to Free Access",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Custom Matrix Grids comparison
                Spacer(modifier = Modifier.height(8.dp))
                Text("Features Matrix Grid Specs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                
                ColComparisonRow("Unlimited CV Layout Portfolios", hasFree = false, hasPro = true)
                ColComparisonRow("Advanced 27-Metric ATS Checkers", hasFree = false, hasPro = true)
                ColComparisonRow("AI Generated Custom Bullet Points", hasFree = true, hasPro = true)
                ColComparisonRow("Persisted SQLite Cover Letter Drafting", hasFree = true, hasPro = true)
                ColComparisonRow("STAR Interview Behavioral Prep Drills", hasFree = false, hasPro = true)
            }

            AnimatedVisibility(
                visible = successUpgradeMsg.isNotBlank(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp)
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = ResumePrimary)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(successUpgradeMsg, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { successUpgradeMsg = "" }) { Text("Dismiss", color = Color.White, fontSize = 10.sp) }
                    }
                }
            }
        }
    }
}

@Composable
fun ColComparisonRow(label: String, hasFree: Boolean, hasPro: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f), color = ResumeDarkText)
            
            Row(modifier = Modifier.weight(0.4f), horizontalArrangement = Arrangement.End) {
                Text(
                    text = if (hasFree) "FREE: Yes" else "FREE: No",
                    fontSize = 10.sp,
                    color = if (hasFree) ResumePrimary else Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = if (hasPro) "PRO: Yes" else "PRO: No",
                    fontSize = 10.sp,
                    color = if (hasPro) ResumePrimary else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
