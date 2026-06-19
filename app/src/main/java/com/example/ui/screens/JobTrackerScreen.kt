package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.JobApplicationEntity
import com.example.ui.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobTrackerScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val applications by viewModel.applications.collectAsState()
    
    val stages = listOf("Wishlist", "Applied", "Interview", "Offer", "Rejected")
    var selectedStageIndex by remember { mutableStateOf(1) } // Default: "Applied"
    
    var showAddDialog by remember { mutableStateOf(false) }
    var companyName by remember { mutableStateOf("") }
    var roleTitle by remember { mutableStateOf("") }
    var appNotes by remember { mutableStateOf("") }
    val currentStageLabel = stages[selectedStageIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Search Kanban Tracker", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ResumePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("tracker_add_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tracked Application")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ResumeLightBg)
        ) {
            // Top Stage Switcher
            ScrollableTabRow(
                selectedTabIndex = selectedStageIndex,
                containerColor = Color.White,
                contentColor = ResumePrimary,
                edgePadding = 16.dp
            ) {
                stages.forEachIndexed { index, stage ->
                    val count = applications.count { it.status == stage }
                    Tab(
                        selected = selectedStageIndex == index,
                        onClick = { selectedStageIndex = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stage, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Badge(containerColor = if (selectedStageIndex == index) ResumePrimary else Color.LightGray) {
                                    Text("$count", color = Color.White)
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filtering apps based on chosen stage
            val filteredApps = applications.filter { it.status == currentStageLabel }

            if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.NextWeek, null, tint = Color.LightGray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No applications in $currentStageLabel",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "Tap the floating button to log one!",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredApps) { app ->
                        var isExpanded by remember { mutableStateOf(false) }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded }
                                .testTag("tracker_card_${app.id}"),
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
                                        Text(
                                            text = app.company,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = ResumeDarkText
                                        )
                                        Text(
                                            text = app.role,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ResumePrimary
                                        )
                                    }

                                    // Left & Right stage slide actions
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (selectedStageIndex > 0) {
                                            IconButton(
                                                onClick = {
                                                    val prevStage = stages[selectedStageIndex - 1]
                                                    viewModel.updateJobApplicationStatus(app.id, prevStage)
                                                },
                                                modifier = Modifier.size(32.dp).testTag("move_prev_${app.id}")
                                            ) {
                                                Icon(Icons.Default.ArrowBackIos, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        
                                        if (selectedStageIndex < stages.size - 1) {
                                            IconButton(
                                                onClick = {
                                                    val nextStage = stages[selectedStageIndex + 1]
                                                    viewModel.updateJobApplicationStatus(app.id, nextStage)
                                                },
                                                modifier = Modifier.size(32.dp).testTag("move_next_${app.id}")
                                            ) {
                                                Icon(Icons.Default.ArrowForwardIos, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }

                                if (isExpanded) {
                                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                                    
                                    if (app.notes.isNotBlank()) {
                                        Text("Recruiter Notes:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text(app.notes, fontSize = 11.sp, color = ResumeDarkText, modifier = Modifier.padding(bottom = 8.dp))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = { viewModel.deleteJobApplication(app.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.08f)),
                                            elevation = null,
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Delete Track", color = Color.Red, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add tracked Application Dialog overlay
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Log Hiring Application") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text("Company Name (e.g. Netflix)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_app_company_input")
                        )

                        OutlinedTextField(
                            value = roleTitle,
                            onValueChange = { roleTitle = it },
                            label = { Text("Role Title (e.g. Senior Staff Scientist)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("add_app_role_input")
                        )

                        OutlinedTextField(
                            value = appNotes,
                            onValueChange = { appNotes = it },
                            label = { Text("Log contacts details, interview notes...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (companyName.isNotBlank() && roleTitle.isNotBlank()) {
                                viewModel.addJobApplication(companyName, roleTitle, currentStageLabel, appNotes)
                                showAddDialog = false
                                companyName = ""
                                roleTitle = ""
                                appNotes = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                        modifier = Modifier.testTag("add_app_confirm_btn")
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
