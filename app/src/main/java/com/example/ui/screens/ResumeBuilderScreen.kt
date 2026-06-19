package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeBuilderScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val activeId by viewModel.activeResumeId.collectAsState()
    val activeTitle by viewModel.activeResumeTitle.collectAsState()
    val activeTemplate by viewModel.activeTemplateId.collectAsState()
    val resumeData by viewModel.activeResumeData.collectAsState()
    val designConfig by viewModel.activeDesignConfig.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiOperationText by viewModel.aiOperationText.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Editor, 1: Live Preview, 2: Design Customizer, 3: AI Assistant Toolbar

    // Section collapse states
    var collapseContact by remember { mutableStateOf(true) }
    var collapseSummary by remember { mutableStateOf(false) }
    var collapseExperience by remember { mutableStateOf(false) }
    var collapseSkills by remember { mutableStateOf(false) }
    var collapseEducation by remember { mutableStateOf(false) }
    var collapseStrengths by remember { mutableStateOf(false) }
    var collapseLanguages by remember { mutableStateOf(false) }

    // Floating success notification variables
    var notificationMessage by remember { mutableStateOf("") }

    // AI Writed Bullets overlay states
    var showAiWriteDialog by remember { mutableStateOf(false) }
    var aiDiagJobTitle by remember { mutableStateOf("") }
    var aiDiagYears by remember { mutableStateOf("3") }
    var aiDiagSkills by remember { mutableStateOf("") }

    // AI Tailor overlay states
    var showAiTailorDialog by remember { mutableStateOf(false) }
    var aiDiagJobDesc by remember { mutableStateOf("") }

    // Translate overlay states
    var showAiTranslateDialog by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("French") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(activeTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Template: $activeTemplate", fontSize = 10.sp, color = ResumePrimary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Export/PDF action button
                    IconButton(
                        onClick = {
                            notificationMessage = "Processing and downloading $activeTitle as elegant, print-ready PDF..."
                        },
                        modifier = Modifier.testTag("builder_export_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download PDF", tint = ResumePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ResumeLightBg)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Section Selector Tabs
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = Color.White,
                    contentColor = ResumePrimary
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("✍️ Form", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("👁️ Preview", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("🎨 Custom", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        text = { Text("⚡ AI tools", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                // Render Content Based On Tab
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (activeTab) {
                        0 -> {
                            // ✍️ Section Form Editor
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                // 1. Title entry
                                item {
                                    OutlinedTextField(
                                        value = activeTitle,
                                        onValueChange = { viewModel.updateResumeTitle(it) },
                                        label = { Text("Document Name") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().background(Color.White).testTag("builder_rename_input")
                                    )
                                }

                                // 2. Contact details Block
                                item {
                                    CollapsibleFormGroup(
                                        title = "1. Personal Contact Info",
                                        icon = Icons.Default.ContactPage,
                                        isCollapsed = collapseContact,
                                        onToggle = { collapseContact = !collapseContact }
                                    ) {
                                        val contacts = resumeData.contactInfo
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedTextField(
                                                value = contacts.name,
                                                onValueChange = { viewModel.updateActiveContactInfo(contacts.copy(name = it)) },
                                                label = { Text("Full Name") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                                            )
                                            OutlinedTextField(
                                                value = contacts.jobTitle,
                                                onValueChange = { viewModel.updateActiveContactInfo(contacts.copy(jobTitle = it)) },
                                                label = { Text("Target Professional Job Title") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth().testTag("edit_job_title_input")
                                            )
                                            OutlinedTextField(
                                                value = contacts.email,
                                                onValueChange = { viewModel.updateActiveContactInfo(contacts.copy(email = it)) },
                                                label = { Text("Email Address") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = contacts.phone,
                                                onValueChange = { viewModel.updateActiveContactInfo(contacts.copy(phone = it)) },
                                                label = { Text("Phone Number") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = contacts.location,
                                                onValueChange = { viewModel.updateActiveContactInfo(contacts.copy(location = it)) },
                                                label = { Text("Location (City, State / Country)") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = contacts.linkedin,
                                                onValueChange = { viewModel.updateActiveContactInfo(contacts.copy(linkedin = it)) },
                                                label = { Text("LinkedIn Handle") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = contacts.github,
                                                onValueChange = { viewModel.updateActiveContactInfo(contacts.copy(github = it)) },
                                                label = { Text("GitHub Profile Link") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = contacts.website,
                                                onValueChange = { viewModel.updateActiveContactInfo(contacts.copy(website = it)) },
                                                label = { Text("Website Portfolio") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }

                                // 3. Summary block
                                item {
                                    CollapsibleFormGroup(
                                        title = "2. Executive Summary Hook",
                                        icon = Icons.Default.StickyNote2,
                                        isCollapsed = collapseSummary,
                                        onToggle = { collapseSummary = !collapseSummary }
                                    ) {
                                        Column {
                                            OutlinedTextField(
                                                value = resumeData.summary,
                                                onValueChange = { viewModel.updateActiveSummary(it) },
                                                label = { Text("Summarize your professional qualifications...") },
                                                modifier = Modifier.fillMaxWidth().height(120.dp).testTag("edit_summary_input")
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.aiImproveBullet(resumeData.summary) { improved ->
                                                        viewModel.updateActiveSummary(improved)
                                                        notificationMessage = "Executive Hook refined successfully with AI!"
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ResumePurple),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.align(Alignment.End).testTag("summary_ai_improve_btn")
                                            ) {
                                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Improve Summary with AI", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }

                                // 4. Work experiences
                                item {
                                    CollapsibleFormGroup(
                                        title = "3. Employment History",
                                        icon = Icons.Default.Work,
                                        isCollapsed = collapseExperience,
                                        onToggle = { collapseExperience = !collapseExperience }
                                    ) {
                                        Column {
                                            if (resumeData.experiences.isEmpty()) {
                                                Text("No experiences listed.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                                            } else {
                                                resumeData.experiences.forEachIndexed { idx, exp ->
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                                        colors = CardDefaults.cardColors(containerColor = ResumeLightBg)
                                                    ) {
                                                        Column(modifier = Modifier.padding(12.dp)) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text("Employer Entry #${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ResumePrimary)
                                                                IconButton(onClick = { viewModel.removeExperience(exp.id) }, modifier = Modifier.size(24.dp)) {
                                                                    Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                                                }
                                                            }
                                                            
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            OutlinedTextField(
                                                                value = exp.company,
                                                                onValueChange = { viewModel.updateExperience(exp.copy(company = it)) },
                                                                label = { Text("Company Name") },
                                                                modifier = Modifier.fillMaxWidth()
                                                            )
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            OutlinedTextField(
                                                                value = exp.position,
                                                                onValueChange = { viewModel.updateExperience(exp.copy(position = it)) },
                                                                label = { Text("Job Position / Role") },
                                                                modifier = Modifier.fillMaxWidth()
                                                            )
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                                OutlinedTextField(
                                                                    value = exp.startDate,
                                                                    onValueChange = { viewModel.updateExperience(exp.copy(startDate = it)) },
                                                                    label = { Text("Start Date") },
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                                OutlinedTextField(
                                                                    value = exp.endDate,
                                                                    onValueChange = { viewModel.updateExperience(exp.copy(endDate = it)) },
                                                                    label = { Text("End Date") },
                                                                    modifier = Modifier.weight(1f),
                                                                    enabled = !exp.isCurrent
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Checkbox(checked = exp.isCurrent, onCheckedChange = { viewModel.updateExperience(exp.copy(isCurrent = it)) })
                                                                Text("Currently working here", fontSize = 11.sp)
                                                            }
                                                            
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            OutlinedTextField(
                                                                value = exp.description,
                                                                onValueChange = { viewModel.updateExperience(exp.copy(description = it)) },
                                                                label = { Text("Roles & Bullets (One per line)") },
                                                                modifier = Modifier.fillMaxWidth().height(100.dp)
                                                            )
                                                            
                                                            // AI improve inline bullet points helper
                                                            Spacer(modifier = Modifier.height(8.dp))
                                                            Button(
                                                                onClick = {
                                                                    viewModel.aiImproveBullet(exp.description) { improved ->
                                                                        viewModel.updateExperience(exp.copy(description = improved))
                                                                        notificationMessage = "Work history phrasing polished successfully!"
                                                                    }
                                                                },
                                                                colors = ButtonDefaults.buttonColors(containerColor = ResumePurple),
                                                                shape = RoundedCornerShape(6.dp)
                                                            ) {
                                                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(12.dp))
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text("Polished Roles with AI", fontSize = 10.sp, color = Color.White)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            
                                            Button(
                                                onClick = {
                                                    viewModel.addExperience(WorkExperience(company = "New Co", position = "Staff Associate"))
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth().testTag("add_exp_btn")
                                            ) {
                                                Icon(Icons.Default.Add, null)
                                                Text("Add Experience Block", fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }

                                // 5. Academic Details
                                item {
                                    CollapsibleFormGroup(
                                        title = "4. Education & Degrees",
                                        icon = Icons.Default.School,
                                        isCollapsed = collapseEducation,
                                        onToggle = { collapseEducation = !collapseEducation }
                                    ) {
                                        Column {
                                            resumeData.education.forEach { edu ->
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                                    colors = CardDefaults.cardColors(containerColor = ResumeLightBg)
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Text(edu.institution.ifEmpty { "School Entry" }, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ResumePrimary)
                                                            IconButton(onClick = { viewModel.removeEducation(edu.id) }, modifier = Modifier.size(24.dp)) {
                                                                    Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                                            }
                                                        }
                                                        OutlinedTextField(
                                                            value = edu.institution,
                                                            onValueChange = { viewModel.updateEducation(edu.copy(institution = it)) },
                                                            label = { Text("School / Institution") },
                                                            modifier = Modifier.fillMaxWidth()
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        OutlinedTextField(
                                                            value = edu.degree,
                                                            onValueChange = { viewModel.updateEducation(edu.copy(degree = it)) },
                                                            label = { Text("Degree / Field of Study") },
                                                            modifier = Modifier.fillMaxWidth()
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        OutlinedTextField(
                                                            value = edu.startDate,
                                                            onValueChange = { viewModel.updateEducation(edu.copy(startDate = it)) },
                                                            label = { Text("Graduation Year") },
                                                            modifier = Modifier.fillMaxWidth()
                                                        )
                                                    }
                                                }
                                            }
                                            Button(
                                                onClick = { viewModel.addEducation(Education(institution = "My University", degree = "B.S. in CS")) },
                                                colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(Icons.Default.Add, null)
                                                Text("Add Education Degree", color = Color.White)
                                            }
                                        }
                                    }
                                }

                                // 6. Skills tag collector
                                item {
                                    CollapsibleFormGroup(
                                        title = "5. Skills & Core Competencies",
                                        icon = Icons.Default.MilitaryTech,
                                        isCollapsed = collapseSkills,
                                        onToggle = { collapseSkills = !collapseSkills }
                                    ) {
                                        var skillName by remember { mutableStateOf("") }
                                        var skillLevel by remember { mutableStateOf(3) }
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedTextField(
                                                    value = skillName,
                                                    onValueChange = { skillName = it },
                                                    label = { Text("Skill Tag (e.g. React)") },
                                                    modifier = Modifier.weight(0.7f),
                                                    singleLine = true
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column(modifier = Modifier.weight(0.3f)) {
                                                    Text("Proficiency", fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                                                    Row {
                                                        repeat(5) { index ->
                                                            Icon(
                                                                imageVector = Icons.Default.Star,
                                                                contentDescription = null,
                                                                tint = if (index < skillLevel) ResumePrimary else Color.LightGray,
                                                                modifier = Modifier.size(16.dp).clickable { skillLevel = index + 1 }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Button(
                                                onClick = {
                                                    if (skillName.isNotBlank()) {
                                                        viewModel.addSkill(Skill(skillName, skillLevel))
                                                        skillName = ""
                                                        skillLevel = 3
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Add Skill Tag", color = Color.White)
                                            }
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                resumeData.skills.forEach { sk ->
                                                    InputChip(
                                                        selected = true,
                                                        onClick = { viewModel.removeSkill(sk.name) },
                                                        label = { Text("${sk.name} (Lvl ${sk.level})") },
                                                        trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(10.dp)) }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // 7. Strengths
                                item {
                                    CollapsibleFormGroup(
                                        title = "6. My Custom Strengths (Enhancv-inspired)",
                                        icon = Icons.Default.FavoriteBorder,
                                        isCollapsed = collapseStrengths,
                                        onToggle = { collapseStrengths = !collapseStrengths }
                                    ) {
                                        var strengthTitle by remember { mutableStateOf("") }
                                        var strengthDesc by remember { mutableStateOf("") }
                                        var strengthIcon by remember { mutableStateOf("Star") }
                                        Column {
                                            OutlinedTextField(
                                                value = strengthTitle,
                                                onValueChange = { strengthTitle = it },
                                                label = { Text("Strength Label (e.g. Action-oriented)") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            OutlinedTextField(
                                                value = strengthDesc,
                                                onValueChange = { strengthDesc = it },
                                                label = { Text("Short description outlining details...") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                listOf("Star", "Bolt", "Favorite", "Lightbulb").forEach { icon ->
                                                    FilterChip(
                                                        selected = strengthIcon == icon,
                                                        onClick = { strengthIcon = icon },
                                                        label = { Text(icon) }
                                                    )
                                                }
                                            }
                                            Button(
                                                onClick = {
                                                    if (strengthTitle.isNotBlank()) {
                                                        viewModel.addStrength(Strength(strengthIcon, strengthTitle, strengthDesc))
                                                        strengthTitle = ""
                                                        strengthDesc = ""
                                                        strengthIcon = "Star"
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Add Strength Vector", color = Color.White)
                                            }
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            resumeData.strengths.forEach { str ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text("• ${str.title} [${str.icon}]", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        Text(str.description, fontSize = 10.sp, color = Color.Gray)
                                                    }
                                                    IconButton(onClick = { viewModel.removeStrength(str.title) }) {
                                                        Icon(Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // 8. Languages
                                item {
                                    CollapsibleFormGroup(
                                        title = "7. Multilingual Passports",
                                        icon = Icons.Default.Translate,
                                        isCollapsed = collapseLanguages,
                                        onToggle = { collapseLanguages = !collapseLanguages }
                                    ) {
                                        var langName by remember { mutableStateOf("") }
                                        var langLvl by remember { mutableStateOf("Fluent") }
                                        Column {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedTextField(
                                                    value = langName,
                                                    onValueChange = { langName = it },
                                                    label = { Text("Language (e.g. Spanish)") },
                                                    modifier = Modifier.weight(0.5f),
                                                    singleLine = true
                                                )
                                                OutlinedTextField(
                                                    value = langLvl,
                                                    onValueChange = { langLvl = it },
                                                    label = { Text("Level (e.g. Native)") },
                                                    modifier = Modifier.weight(0.5f),
                                                    singleLine = true
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Button(
                                                onClick = {
                                                    if (langName.isNotBlank()) {
                                                        viewModel.addLanguage(Language(langName, langLvl))
                                                        langName = ""
                                                        langLvl = "Fluent"
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Add Language Passport", color = Color.White)
                                            }
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            resumeData.languages.forEach { lang ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("${lang.name}: ${lang.level}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    IconButton(onClick = { viewModel.removeLanguage(lang.name) }) {
                                                        Icon(Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // 👁️ Live Preview Layout Page
                            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                ResumePreview(
                                    data = resumeData,
                                    design = designConfig,
                                    templateId = activeTemplate,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        2 -> {
                            // 🎨 Design Customizer Sidebar Panel
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Resume Styling Config", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ResumeDarkText)
                                
                                // Template selection list
                                Divider()
                                Text("Choose structural design layout:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val layouts = listOf("Double Column", "Ivy League", "Elegant", "Minimal")
                                    layouts.forEach { lay ->
                                        FilterChip(
                                            selected = activeTemplate == lay,
                                            onClick = { viewModel.changeActiveTemplate(lay) },
                                            label = { Text(lay) }
                                        )
                                    }
                                }

                                // 1. Font Family options
                                Divider()
                                Text("Global typography font family:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val fonts = listOf("Sans-Serif" to "Inter", "Serif" to "Serif", "Monospace" to "Monospace")
                                    fonts.forEach { (lbl, valStr) ->
                                        ElevatedCard(
                                            // click on modifier
                                            modifier = if (designConfig.fontFamily == valStr) Modifier.weight(1f).border(1.dp, ResumePrimary, RoundedCornerShape(8.dp)).clickable { viewModel.updateDesign(designConfig.copy(fontFamily = valStr)) } else Modifier.weight(1f).clickable { viewModel.updateDesign(designConfig.copy(fontFamily = valStr)) },
                                            colors = CardDefaults.elevatedCardColors(
                                                containerColor = if (designConfig.fontFamily == valStr) ResumePrimary.copy(alpha = 0.1f) else Color.White
                                            ),
                                            // border handled by modifier
                                        ) {
                                            Box(modifier = Modifier.padding(8.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                                Text(lbl, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (designConfig.fontFamily == valStr) ResumePrimary else ResumeDarkText)
                                            }
                                        }
                                    }
                                }

                                // 2. Font size multiplier slider
                                Divider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Heading Font Sizing Factor", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text("%.1f x".format(designConfig.fontSizeMultiplier), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ResumePrimary)
                                }
                                Slider(
                                    value = designConfig.fontSizeMultiplier,
                                    onValueChange = { viewModel.updateDesign(designConfig.copy(fontSizeMultiplier = it)) },
                                    valueRange = 0.8f..1.4f,
                                    steps = 6,
                                    colors = SliderDefaults.colors(thumbColor = ResumePrimary, activeTrackColor = ResumePrimary)
                                )

                                // 3. Primary Color Pickers
                                Divider()
                                Text("Primary branding accent theme color:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val hexColors = listOf("#2D9E72", "#115E59", "#1E3A8A", "#9D174D", "#D97706", "#7C3AED")
                                    hexColors.forEach { h ->
                                        val isActive = designConfig.primaryColor == h
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(Color(android.graphics.Color.parseColor(h)))
                                                .border(
                                                    width = if (isActive) 3.dp else 1.dp,
                                                    color = if (isActive) ResumeDarkText else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable { viewModel.updateDesign(designConfig.copy(primaryColor = h)) }
                                        )
                                    }
                                }

                                // 4. Padding density constraints
                                Divider()
                                Text("Document padding & density margins:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Compact", "Normal", "Spacious").forEach { sz ->
                                        val isSel = designConfig.spacing == sz
                                        ElevatedCard(
                                            onClick = { viewModel.updateDesign(designConfig.copy(spacing = sz)) },
                                            modifier = Modifier.weight(1f),
                                            colors = CardDefaults.elevatedCardColors(
                                                containerColor = if (isSel) ResumePrimary.copy(alpha = 0.1f) else Color.White
                                            )
                                        ) {
                                            Box(modifier = Modifier.padding(10.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                                Text(sz, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            // ⚡ AI tools tab panel
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("Intellectual AI Assistant Actions", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ResumeDarkText)
                                Text("Unleash the Gemini model to rewrite, optimize or polish your drafting materials.", fontSize = 11.sp, color = Color.Gray)
                                
                                Spacer(modifier = Modifier.height(10.dp))

                                // AI tool button 1
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showAiWriteDialog = true },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.BorderColor, null, tint = ResumePrimary)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Generate Bullets from Scratch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Draft 4 strong executive bullets matching a job category.", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                }

                                // AI tool button 2
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showAiTailorDialog = true },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CloudSync, null, tint = ResumePurple)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("One-Click Target Job Tailor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Paste target requirements to rewrite summary to fit standard terms.", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                }

                                // AI tool button 3
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.aiFixGrammarAll() },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.FactCheck, null, tint = ResumePrimary)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Complete Phrasing Proofreader", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Scan spelling grammar structural layouts mistakes.", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                }

                                // AI tool button 4
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showAiTranslateDialog = true },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Translate, null, tint = ResumePurple)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Translate Resume to 30+ Languages", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Instantly convert documents, headers, roles and details.", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                }

                                // AI result text displays
                                val resultText by viewModel.aiResultText.collectAsState()
                                if (resultText.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, ResumePrimary.copy(alpha = 0.4f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("AI Response Output:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ResumePrimary)
                                                IconButton(onClick = { viewModel.updateActiveSummary(resultText) }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Check, null, tint = ResumePrimary)
                                                }
                                            }
                                            Text(resultText, fontSize = 10.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // AI loading floating HUD overlay spinner
            if (isAiLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = ResumePrimary)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(aiOperationText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Saving Success notification overlay banner
            AnimatedVisibility(
                visible = notificationMessage.isNotBlank(),
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ResumePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(notificationMessage, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        TextButton(onClick = { notificationMessage = "" }) {
                            Text("Dismis", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // 1. Generate Bullets dialog
        if (showAiWriteDialog) {
            AlertDialog(
                onDismissRequest = { showAiWriteDialog = false },
                title = { Text("AI Bullet Generator") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = aiDiagJobTitle, onValueChange = { aiDiagJobTitle = it }, label = { Text("Target Position Job Title") }, modifier = Modifier.fillMaxWidth().testTag("ai_diag_title_input"))
                        OutlinedTextField(value = aiDiagYears, onValueChange = { aiDiagYears = it }, label = { Text("Years of Experience") }, modifier = Modifier.fillMaxWidth().testTag("ai_diag_years_input"))
                        OutlinedTextField(value = aiDiagSkills, onValueChange = { aiDiagSkills = it }, label = { Text("Relevant skills to highlight") }, modifier = Modifier.fillMaxWidth().testTag("ai_diag_skills_input"))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (aiDiagJobTitle.isNotBlank()) {
                                viewModel.aiWriteBullets(aiDiagJobTitle, aiDiagYears, aiDiagSkills) { bullets ->
                                    val compiled = bullets.joinToString("\n")
                                    viewModel.addExperience(WorkExperience(company = "Selected Company", position = aiDiagJobTitle, description = compiled))
                                    showAiWriteDialog = false
                                    notificationMessage = "Inserted AI-generated experiences timeline item successfully!"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary)
                    ) {
                        Text("Write Bullets")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAiWriteDialog = false }) { Text("Cancel") }
                }
            )
        }

        // 2. Tailor dialog
        if (showAiTailorDialog) {
            AlertDialog(
                onDismissRequest = { showAiTailorDialog = false },
                title = { Text("Tailor Resume to JD") },
                text = {
                    OutlinedTextField(
                        value = aiDiagJobDesc,
                        onValueChange = { aiDiagJobDesc = it },
                        label = { Text("Paste Job Description specifications herein...") },
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (aiDiagJobDesc.isNotBlank()) {
                                viewModel.aiTailorResumeToJob(aiDiagJobDesc) { tailored ->
                                    viewModel.updateActiveSummary(tailored)
                                    showAiTailorDialog = false
                                    notificationMessage = "Tailored summary rewrote with priority keys!"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary)
                    ) {
                        Text("Optimize")
                    }
                }
            )
        }

        // 3. Translate Dialog
        if (showAiTranslateDialog) {
            AlertDialog(
                onDismissRequest = { showAiTranslateDialog = false },
                title = { Text("Translate Resume") },
                text = {
                    val languages = listOf("Spanish", "German", "French", "Japanese", "Hindi", "Chinese", "Italian")
                    Column {
                        Text("Select Target Language:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(10.dp))
                        languages.forEach { lng ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedLanguage = lng }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(lng, fontSize = 13.sp)
                                if (selectedLanguage == lng) {
                                    Icon(Icons.Default.Check, null, tint = ResumePrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.aiTranslateResumeAll(selectedLanguage) { translated ->
                                viewModel.updateActiveSummary(translated)
                                showAiTranslateDialog = false
                                notificationMessage = "Translated and saved resume sections into $selectedLanguage!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary)
                    ) {
                        Text("Translate Draft")
                    }
                }
            )
        }
    }
}

@Composable
fun CollapsibleFormGroup(
    title: String,
    icon: ImageVector,
    isCollapsed: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("collapse_group_${title.substringBefore(".")}"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = ResumePrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                }
                Icon(
                    imageVector = if (isCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
            }
            if (!isCollapsed) {
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }
}
