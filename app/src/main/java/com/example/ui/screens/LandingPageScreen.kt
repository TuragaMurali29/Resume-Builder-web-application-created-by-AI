package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel
import com.example.ui.theme.*

@Composable
fun LandingPageScreen(
    viewModel: AppViewModel,
    onNavToAuth: () -> Unit,
    onNavToChecker: () -> Unit,
    onNavToBuilder: () -> Unit,
    onNavToPricing: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ResumeLightBg)
    ) {
        // Aesthetic Gradient Blobs in Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ResumePrimary.copy(alpha = 0.12f),
                            ResumePurple.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header Logo bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = ResumePrimary,
                        modifier = Modifier.size(28.dp).padding(end = 4.dp)
                    )
                    Text(
                        text = "ResumeAI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = ResumeDarkText,
                        letterSpacing = (-0.5).sp
                    )
                }
                
                Button(
                    onClick = onNavToAuth,
                    colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("landing_get_started_btn")
                ) {
                    Text("Get Started", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // --- 1. HERO SECTION ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Floating Social Proof Badge
                Surface(
                    color = ResumePurple.copy(alpha = 0.15f),
                    contentColor = ResumeDarkText,
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ResumePrimary,
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                        Text(
                            text = "28,000+ users landed interviews last month",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = "Land More Interviews\nwith AI Resume Builder",
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = ResumeDarkText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "Build recruiter-approved, ATS-optimized resumes and matching cover letters in minutes with our world-class AI engine.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Two CTAs
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavToBuilder,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("hero_build_cv_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Build Resume", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onNavToChecker,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("hero_check_score_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ResumePrimary),
                        border = BorderStroke(1.5.dp, ResumePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.QueryStats, null, tint = ResumePrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check Score", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- 2. FLOAT CAROUSEL OF TEMPLATES ---
            TemplateCarouselSection()

            // --- 3. STATS DASHBOARD BAR ---
            StatsDashboardSection()

            // --- 4. TABBED INSTRUCTION: HOW WE HELP ---
            HowHelpTabbedSection(onNavToBuilder, onNavToChecker)

            // --- 5. ATS COMPLIANCE INFORMATION INFO BLOCK ---
            AtsOptimizationSection()

            // --- 6. FEATURING SERVICES GRID ---
            FeatureGridSection()

            // --- 7. ACCORDION FAQ BLOCK ---
            AccordionFaqSection()

            // --- 8. TESTIMONIAL CAROUSEL ---
            TestimonialsSection()

            // --- 9. RECENT RECRUITING BLOG ARTICLES ---
            BlogGridSection()

            // --- 10. POLISHED FOOTER ---
            RecruiterFooterSection(onNavToPricing)
        }
    }
}

@Composable
fun TemplateCarouselSection() {
    val templates = listOf(
        Triple("Double Column", "Dynamic, layout-optimized modern classic", ResumePrimary),
        Triple("Ivy League", "Black & white high-prestige executive standard", ResumeDarkText),
        Triple("Elegant", "Colored banners with beautiful layout profiles", ResumePurple),
        Triple("Minimal", "Sleek, high spacing clean professional minimalist", ResumePrimary)
    )

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Recruiter-Approved Styling Themes",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ResumeDarkText,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        Text(
            text = "Apply standard compliance structures with a single tap.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            templates.forEach { (name, desc, color) ->
                Card(
                    modifier = Modifier
                        .width(180.dp)
                        .height(130.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Dashboard, null, tint = color, modifier = Modifier.size(14.dp))
                        }
                        
                        Column {
                            Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                            Text(text = desc, fontSize = 10.sp, color = Color.Gray, lineHeight = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsDashboardSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ResumeDarkBg)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("15M+", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ResumeAccentGreen)
            Text("Resumes Created", fontSize = 9.sp, color = Color.LightGray)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("84%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ResumePurple)
            Text("ATS Pass Rate", fontSize = 9.sp, color = Color.LightGray)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("1M+", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ResumePeach)
            Text("Monthly Readers", fontSize = 9.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun HowHelpTabbedSection(onNavToBuilder: () -> Unit, onNavToChecker: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) }
    val tabsList = listOf(
        "Resume Builder" to Icons.Default.BorderColor,
        "Resume Checker" to Icons.Default.QueryStats,
        "Cover Letter" to Icons.Default.Email,
        "Job Tracker" to Icons.Default.Dashboard,
        "Interview Help" to Icons.Default.Forum
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Complete Career Accelerator",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = ResumeDarkText,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Text(
            text = "Everything you need to secure your target interview.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
        )

        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.Transparent,
            edgePadding = 16.dp,
            indicator = { TabRowDefaults.Indicator(color = ResumePrimary) }
        ) {
            tabsList.forEachIndexed { idx, (title, icon) ->
                Tab(
                    selected = activeTab == idx,
                    onClick = { activeTab = idx },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(icon, null, modifier = Modifier.size(16.dp)) },
                    selectedContentColor = ResumePrimary,
                    unselectedContentColor = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (activeTab) {
                    0 -> {
                        Text("Guided Enhancv-Style Editor", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                        Text("Fill in sections dynamically including Work Experience, Strengths, Books, Hobbies and languages. Each section contains drag reordering and instant visual updates.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 6.dp))
                        Button(onClick = onNavToBuilder, colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary), shape = RoundedCornerShape(8.dp)) {
                            Text("Launch Builder", color = Color.White)
                        }
                    }
                    1 -> {
                        Text("27-Metric Comprehensive ATS Audit", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                        Text("Scan formatting compliance, keyword inclusion vectors, sentence structures and pronoun breaches with one button. Instant score updates out of 100.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 6.dp))
                        Button(onClick = onNavToChecker, colors = ButtonDefaults.buttonColors(containerColor = ResumePrimary), shape = RoundedCornerShape(8.dp)) {
                            Text("Audit My Resume", color = Color.White)
                        }
                    }
                    2 -> {
                        Text("AI Cover Letter Architect", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                        Text("Pull context directly from your active resume and target company description. Generates custom cover letters written to align matching design styles.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 6.dp))
                    }
                    3 -> {
                        Text("Kanban Board Job Tracker", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                        Text("Visualize your progress through Wishlist, Applied, Interview, Offer, and Rejected. Log interview questions and notes dynamically in SQLite database.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 6.dp))
                    }
                    4 -> {
                        Text("STAR Interview AI Preparation", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                        Text("Generate top likely job interview questions, complete behavioral guidelines (STAR), and custom elevator pitches in seconds utilizing backend AI models.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AtsOptimizationSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ResumePurple.copy(alpha = 0.08f))
            .border(1.dp, ResumePurple.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudSync, null, tint = ResumePurple, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Is your resume ATS Compliant?", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ResumeDarkText)
            }
            Text(
                text = "Most legacy applicant tracking systems parse and strip out complex graphics, non-standard visual blocks, and vertical layouts. ResumeAI templates are meticulously coded in lightweight, compliant structures to achieve 100% scanner readable standards.",
                fontSize = 12.sp,
                color = ResumeDarkText.copy(alpha = 0.8f),
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun FeatureGridSection() {
    val features = listOf(
        Pair("AI Proofreading", "Correct grammar, terminology, and spacing issues instantly in one tap."),
        Pair("One-Click Tailoring", "Read target JDs and rewrite key bullet points to align candidate keywords."),
        Pair("20+ Resume Sections", "Includes Strengths, Quotes, Daily routines, Languages and classic Work records."),
        Pair("ATS Keyword Checker", "Audits priority matching terms to rank your compliance scores out of 100.")
    )

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
        Text("Engineered for Placement", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ResumeDarkText)
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            features.forEach { (title, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ResumePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText)
                            Text(desc, fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccordionFaqSection() {
    val faqs = listOf(
        "Are these templates ATS-friendly?" to "Yes! Our templates are explicitly coded following standard chronological layout systems guidelines to avoid parser strip-out mistakes.",
        "How many languages are supported?" to "We support translating your resume and cover letters into 30+ major languages using our integrated Gemini engine.",
        "What is the quantitative metrics rule?" to "Recruiters prefer numbers. Bullet points displaying clear metrics (e.g., 'improved page-load from 4s to 1.8s' or 'scaled team of 5') achieve significantly higher screening outcomes.",
        "Can I create multiple resumes?" to "Yes, the dashboard allows you to duplicate, rename, duplicate styles, and customize as many documents as your job search requires.",
        "How does the STAR format work?" to "STAR stands for Situation, Task, Action, and Result. Our prep module outlines answers exactly matching this behavior to make your interview responses extremely technical and high impact.",
        "Is there a limit on cover letters?" to "No! You can save and coordinate matching cover letters for each employers entry draft saved in the tracker."
    )

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Frequently Answered FAQs", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ResumeDarkText)
        Spacer(modifier = Modifier.height(12.dp))

        faqs.forEach { (q, a) ->
            var expanded by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = q,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResumeDarkText,
                        modifier = Modifier.weight(0.9f)
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ResumePrimary
                    )
                }
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = a,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TestimonialsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ResumeDarkBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Praised by Global Candidates", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ResumeAccentGreen)
        Text("Star Ratings & Reviews", fontSize = 12.sp, color = Color.LightGray)

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) {
                Icon(Icons.Default.Star, null, tint = ResumePeach, modifier = Modifier.size(18.dp))
            }
        }
        
        Text(
            text = "\"Using the ResumeAI Double Column layout, my resume passed screening for 11 out of 14 React job applications! The automated ATS score auditor identified my spelling and keyword gaps in minutes.\"",
            fontSize = 12.sp,
            color = Color.White,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        Text("- Marcus K., Senior Dev", fontSize = 11.sp, color = ResumeAccentGreen, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BlogGridSection() {
    val blogs = listOf(
        "Wording Your Experience Quantitative Metrics",
        "Classic Fonts vs Modern Typography in ATS Systems",
        "Landing FAANG interviews: Quick compliance rules",
        "How to avoid first-person pronouns in outlines",
        "Writing matching cover letter hooks templates",
        "Job Tracker checklists: Kanban pipelines guidelines"
    )

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Recent Career Counsel blogs", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ResumeDarkText)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            blogs.forEach { title ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .height(110.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ResumeDarkText, maxLines = 3)
                        Text("Read blog  →", fontSize = 9.sp, color = ResumePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RecruiterFooterSection(onNavToPricing: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ResumeDarkText)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Cover Letter", fontSize = 11.sp, color = Color.White, modifier = Modifier.clickable { })
            Text("FAQ", fontSize = 11.sp, color = Color.White, modifier = Modifier.clickable { })
            Text("Pricing Plans", fontSize = 11.sp, color = Color.White, modifier = Modifier.clickable { onNavToPricing() })
            Text("Resume Blog", fontSize = 11.sp, color = Color.White, modifier = Modifier.clickable { })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("© 2026 ResumeAI and career solutions portfolio. Built for optimal placement.", fontSize = 9.sp, color = Color.LightGray)
    }
}
