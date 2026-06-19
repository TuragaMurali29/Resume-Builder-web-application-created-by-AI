package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onEditResume: () -> Unit,
    onEditCoverLetter: () -> Unit,
    onNavToChecker: () -> Unit,
    onNavToTracker: () -> Unit,
    onNavToPrep: () -> Unit,
    onNavToJobSearch: () -> Unit,
    onNavToPricing: () -> Unit,
    onLogout: () -> Unit
) {
    // Current Dashboard View state
    // "resumes", "letters", "settings"
    var currentSubView by remember { mutableStateOf("resumes") }

    val resumes by viewModel.resumes.collectAsState()
    val coverLetters by viewModel.coverLetters.collectAsState()
    val userPlan by viewModel.userPlan.collectAsState()
    val userName by viewModel.userName.collectAsState()

    var showCreateResumeDialog by remember { mutableStateOf(false) }
    var newResumeTitle by remember { mutableStateOf("") }
    var newResumeTemplate by remember { mutableStateOf("Double Column") }

    var showCreateLetterDialog by remember { mutableStateOf(false) }
    var newLetterTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dashboard, null, tint = ResumePrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ResumeAI Portal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    Surface(
                        color = if (userPlan == "Pro") ResumePrimary else ResumePurple,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 12.dp).clickable { onNavToPricing() }
                    ) {
                        Text(
                            text = if (userPlan == "Pro") "PRO" else "FREE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = currentSubView == "resumes",
                    onClick = { currentSubView = "resumes" },
                    icon = { Icon(Icons.Default.Description, null) },
                    label = { Text("Resumes", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = ResumePrimary, selectedTextColor = ResumePrimary)
                )
                NavigationBarItem(
                    selected = currentSubView == "letters",
                    onClick = { currentSubView = "letters" },
                    icon = { Icon(Icons.Default.Email, null) },
                    label = { Text("Cover Letters", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = ResumePrimary, selectedTextColor = ResumePrimary)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavToTracker() },
                    icon = { Icon(Icons.Default.Dashboard, null) },
                    label = { Text("Job Tracker", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavToChecker() },
                    icon = { Icon(Icons.Default.QueryStats, null) },
                    label = { Text("ATS Checker", fontSize = 11.sp) }
                )
            }
        },
        floatingActionButton = {
            if (currentSubView == "resumes") {
                FloatingActionButton(
                    onClick = { showCreateResumeDialog = true },
                    containerColor = ResumePrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("dashboard_add_resume_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Resume")
                }
            } else if (currentSubView == "letters") {
                FloatingActionButton(
                    onClick = { showCreateLetterDialog = true },
                    containerColor = ResumePrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("dashboard_add_letter_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Cover Letter")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ResumeLightBg)
        ) {
            // Welcome Header message banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ResumePurple.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, ResumePurple.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hello, $userName!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ResumeDarkText
                    )
                    Text(
                        text = "Build optimized portfolios to beat standard ATS parser compliance algorithms.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    
                    // Quick Action grid shortcuts
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionShortcutChip("Job Scout", Icons.Default.Search, onNavToJobSearch, Modifier.weight(1f))
                        ActionShortcutChip("Interview Qs", Icons.Default.Forum, onNavToPrep, Modifier.weight(1f))
                        ActionShortcutChip("Pro Perks", Icons.Default.Star, onNavToPricing, Modifier.weight(1f))
                    }
                }
            }
            
            // Render Selected Portions
            if (currentSubView == "resumes") {
                ResumesListView(
                    resumes = resumes,
                    viewModel = viewModel,
                    onEditResume = onEditResume
                )
            } else {
                CoverLettersListView(
                    coverLetters = coverLetters,
                    viewModel = viewModel,
                    onEditCoverLetter = onEditCoverLetter
                )
            }
        }

        // Dialog for New Resume
        if (showCreateResumeDialog) {
            AlertDialog(
                onDismissRequest = { showCreateResumeDialog = false },
                title = { Text("Create Resume AI") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = newResumeTitle,
                            onValueChange = { newResumeTitle = it },
                            label = { Text("Resume Title (e.g. React Lead CV)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("dialog_resume_title_input")
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Select Initial Template Style:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val templates = listOf("Double Column", "Ivy League", "Elegant", "Minimal")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            templates.forEach { tmpl ->
                                FilterChip(
                                    selected = newResumeTemplate == tmpl,
                                    onClick = { newResumeTemplate = tmpl },
                                    label = { Text(tmpl, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newResumeTitle.isNotBlank()) {
                                viewModel.selectNewBlankResume(newResumeTitle, newResumeTemplate)
                                showCreateResumeDialog = false
                                newResumeTitle = ""
                                onEditResume()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                        modifier = Modifier.testTag("dialog_resume_confirm_btn")
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateResumeDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog for New Cover Letter
        if (showCreateLetterDialog) {
            AlertDialog(
                onDismissRequest = { showCreateLetterDialog = false },
                title = { Text("New Cover Letter") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = newLetterTitle,
                            onValueChange = { newLetterTitle = it },
                            label = { Text("Letter Title (e.g. Meta Letter)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("dialog_letter_title_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newLetterTitle.isNotBlank()) {
                                viewModel.selectNewBlankCoverLetter(newLetterTitle)
                                showCreateLetterDialog = false
                                newLetterTitle = ""
                                onEditCoverLetter()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                        modifier = Modifier.testTag("dialog_letter_confirm_btn")
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateLetterDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ActionShortcutChip(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = ResumePrimary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
        }
    }
}

@Composable
fun ResumesListView(
    resumes: List<ResumeEntity>,
    viewModel: AppViewModel,
    onEditResume: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    if (resumes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddBox, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                Text("No resumes found", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                Text("Tap the floating button to create one!", fontSize = 11.sp, color = Color.Gray)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 6.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(resumes) { resume ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectActiveResume(resume)
                            onEditResume()
                        }
                        .testTag("resume_card_${resume.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Quick visual display top
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = resume.templateId,
                                fontSize = 10.sp,
                                color = ResumePrimary,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // ATS score badge indicator
                            val scoreColor = when {
                                resume.atsScore >= 75 -> ResumePrimary
                                resume.atsScore >= 50 -> ResumePurple
                                else -> Color.Red
                            }
                            Surface(
                                color = scoreColor.copy(alpha = 0.12f),
                                contentColor = scoreColor,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Analytics, null, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("ATS: ${resume.atsScore}", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = resume.title.ifEmpty { "Untitled Resume" },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ResumeDarkText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Edited: " + SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(resume.updatedAt)),
                            fontSize = 10.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {
                                    // Duplicate resume
                                    coroutineScope.launch {
                                        viewModel.selectActiveResume(resume)
                                        viewModel.updateResumeTitle("${resume.title} (Duplicate)")
                                    }
                                },
                                modifier = Modifier.size(24.dp).testTag("resume_duplicate_btn_${resume.id}")
                            ) {
                                Icon(Icons.Default.CopyAll, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.deleteResume(resume.id) },
                                modifier = Modifier.size(24.dp).testTag("resume_delete_btn_${resume.id}")
                            ) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoverLettersListView(
    coverLetters: List<CoverLetterEntity>,
    viewModel: AppViewModel,
    onEditCoverLetter: () -> Unit
) {
    if (coverLetters.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Email, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                Text("No cover letters found", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                Text("Tap the floating button to write matching cover letters!", fontSize = 11.sp, color = Color.Gray)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 6.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(coverLetters) { letter ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectActiveCoverLetter(letter)
                            onEditCoverLetter()
                        }
                        .testTag("letter_card_${letter.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(0.8f)) {
                            Text(
                                text = letter.title.ifEmpty { "Untitled Cover Letter" },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ResumeDarkText
                            )
                            Text(
                                text = "Applying as ${letter.jobTitle} at ${letter.company}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        
                        IconButton(
                            onClick = { viewModel.deleteCoverLetter(letter.id) },
                            modifier = Modifier.size(28.dp).testTag("letter_delete_btn_${letter.id}")
                        ) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
