package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@Composable
fun ResumePreview(
    data: ResumeData,
    design: DesignConfig,
    templateId: String,
    modifier: Modifier = Modifier
) {
    // Resolve styling variables
    val primaryColor = try { Color(android.graphics.Color.parseColor(design.primaryColor)) } catch (e: Exception) { Color(0xFF2D9E72) }
    val accentColor = try { Color(android.graphics.Color.parseColor(design.accentColor)) } catch (e: Exception) { Color(0xFFA396E2) }
    val baseFont = when (design.fontFamily) {
        "Serif" -> FontFamily.Serif
        "Monospace" -> FontFamily.Monospace
        else -> FontFamily.SansSerif
    }
    
    val spacingDp = when (design.spacing) {
        "Compact" -> 4.dp
        "Spacious" -> 16.dp
        else -> 8.dp
    }
    
    val scale = design.fontSizeMultiplier

    Card(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (templateId) {
                "Ivy League" -> IvyLeagueTemplate(data, baseFont, scale, spacingDp, primaryColor)
                "Elegant" -> ElegantTemplate(data, baseFont, scale, spacingDp, primaryColor, accentColor)
                "Minimal" -> MinimalTemplate(data, baseFont, scale, spacingDp, primaryColor)
                else -> DoubleColumnTemplate(data, baseFont, scale, spacingDp, primaryColor, accentColor)
            }
        }
    }
}

@Composable
private fun HeaderBlock(
    info: ContactInfo,
    fontFamily: FontFamily,
    scale: Float,
    primaryColor: Color,
    alignment: Alignment.Horizontal = Alignment.Start,
    isSerif: Boolean = false
) {
    val textAlign = if (alignment == Alignment.CenterHorizontally) TextAlign.Center else TextAlign.Start
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Text(
            text = info.name.ifEmpty { "Full Name" },
            fontFamily = fontFamily,
            fontSize = (24 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = if (isSerif) Color.Black else primaryColor,
            textAlign = textAlign
        )
        if (info.jobTitle.isNotBlank()) {
            Text(
                text = info.jobTitle.uppercase(),
                fontFamily = fontFamily,
                fontSize = (12 * scale).sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                letterSpacing = 1.5.sp,
                textAlign = textAlign,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Wrap contact details flowably
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (alignment == Alignment.CenterHorizontally) Arrangement.Center else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val contactItems = listOfNotNull(
                if (info.email.isNotBlank()) info.email else null,
                if (info.phone.isNotBlank()) info.phone else null,
                if (info.location.isNotBlank()) info.location else null
            )
            Text(
                text = contactItems.joinToString(" • "),
                fontFamily = fontFamily,
                fontSize = (10 * scale).sp,
                color = Color.DarkGray,
                textAlign = textAlign
            )
        }
        
        // Digital handles
        val links = listOfNotNull(
            if (info.website.isNotBlank()) info.website else null,
            if (info.linkedin.isNotBlank()) info.linkedin else null,
            if (info.github.isNotBlank()) info.github else null
        )
        if (links.isNotEmpty()) {
            Text(
                text = links.joinToString(" | "),
                fontFamily = fontFamily,
                fontSize = (9 * scale).sp,
                color = primaryColor.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                textAlign = textAlign,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    fontFamily: FontFamily,
    scale: Float,
    primaryColor: Color,
    hasLine: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = title.uppercase(),
            fontFamily = fontFamily,
            fontSize = (13 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            letterSpacing = 1.2.sp
        )
        if (hasLine) {
            Divider(
                color = primaryColor.copy(alpha = 0.3f),
                thickness = 1.5.dp,
                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
            )
        }
    }
}

// --- 1. DOUBLE COLUMN TEMPLATE ---
@Composable
private fun DoubleColumnTemplate(
    data: ResumeData,
    fontFamily: FontFamily,
    scale: Float,
    spacing: Dp,
    primaryColor: Color,
    accentColor: Color
) {
    HeaderBlock(data.contactInfo, fontFamily, scale, primaryColor)
    Spacer(modifier = Modifier.height(12.dp))
    
    if (data.summary.isNotBlank()) {
        Text(
            text = data.summary,
            fontFamily = fontFamily,
            fontSize = (11 * scale).sp,
            color = Color.DarkGray,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(spacing))
    }
    
    Row(modifier = Modifier.fillMaxWidth()) {
        // Main Column (60% width)
        Column(modifier = Modifier.weight(0.62f).padding(end = 8.dp)) {
            // Work Experience
            if (data.experiences.isNotEmpty()) {
                SectionHeading("Experience", fontFamily, scale, primaryColor)
                data.experiences.forEach { exp ->
                    Column(modifier = Modifier.padding(bottom = spacing)) {
                        Text(
                            text = exp.position,
                            fontFamily = fontFamily,
                            fontSize = (12 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = exp.company,
                                fontFamily = fontFamily,
                                fontSize = (10 * scale).sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor
                            )
                            Text(
                                text = "${exp.startDate} – ${if (exp.isCurrent) "Present" else exp.endDate}",
                                fontFamily = fontFamily,
                                fontSize = (10 * scale).sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        exp.description.split("\n").forEach { bullet ->
                            val cleanBullet = bullet.trim().removePrefix("-").trim()
                            if (cleanBullet.isNotEmpty()) {
                                Row(modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 2.dp)) {
                                    Text(
                                        text = "•",
                                        color = primaryColor,
                                        fontSize = (10 * scale).sp,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                    Text(
                                        text = cleanBullet,
                                        fontFamily = fontFamily,
                                        fontSize = (10 * scale).sp,
                                        color = Color.DarkGray,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Projects list
            if (data.projects.isNotEmpty()) {
                SectionHeading("Projects", fontFamily, scale, primaryColor)
                data.projects.forEach { proj ->
                    Column(modifier = Modifier.padding(bottom = spacing)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = proj.name,
                                fontFamily = fontFamily,
                                fontSize = (11 * scale).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            if (proj.url.isNotBlank()) {
                                Text(
                                    text = proj.url,
                                    fontFamily = fontFamily,
                                    fontSize = (9 * scale).sp,
                                    color = accentColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        if (proj.role.isNotBlank()) {
                            Text(
                                text = proj.role,
                                fontFamily = fontFamily,
                                fontSize = (9 * scale).sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = proj.description,
                            fontFamily = fontFamily,
                            fontSize = (10 * scale).sp,
                            color = Color.DarkGray,
                            lineHeight = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
        
        // Sidebar Column (38% width)
        Column(modifier = Modifier.weight(0.38f).padding(start = 8.dp)) {
            // Skills Section
            if (data.skills.isNotEmpty()) {
                SectionHeading("Skills", fontFamily, scale, primaryColor)
                data.skills.forEach { skill ->
                    Column(modifier = Modifier.padding(bottom = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = skill.name,
                                fontFamily = fontFamily,
                                fontSize = (10 * scale).sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Row {
                                repeat(5) { index ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (index < skill.level) primaryColor else Color.LightGray.copy(alpha = 0.5f)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(spacing))
            }
            
            // Strengths Section (Inspired by Enhancv)
            if (data.strengths.isNotEmpty()) {
                SectionHeading("My Strengths", fontFamily, scale, primaryColor)
                data.strengths.forEach { str ->
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = when (str.icon) {
                                "Bolt" -> Icons.Default.ElectricBolt
                                "Favorite" -> Icons.Default.Favorite
                                "Lightbulb" -> Icons.Default.Lightbulb
                                "Face" -> Icons.Default.EmojiPeople
                                else -> Icons.Default.Star
                            },
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(14.dp).padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = str.title,
                                fontFamily = fontFamily,
                                fontSize = (10 * scale).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = str.description,
                                fontFamily = fontFamily,
                                fontSize = (9 * scale).sp,
                                color = Color.DarkGray,
                                lineHeight = 11.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(spacing))
            }

            // Education Section
            if (data.education.isNotEmpty()) {
                SectionHeading("Education", fontFamily, scale, primaryColor)
                data.education.forEach { edu ->
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = edu.degree,
                            fontFamily = fontFamily,
                            fontSize = (10 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = edu.institution,
                            fontFamily = fontFamily,
                            fontSize = (9 * scale).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )
                        Text(
                            text = "${edu.startDate} – ${if (edu.isCurrent) "Present" else edu.endDate}",
                            fontFamily = fontFamily,
                            fontSize = (9 * scale).sp,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(spacing))
            }

            // Languages Section
            if (data.languages.isNotEmpty()) {
                SectionHeading("Languages", fontFamily, scale, primaryColor)
                data.languages.forEach { lang ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = lang.name,
                            fontFamily = fontFamily,
                            fontSize = (10 * scale).sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = lang.level,
                            fontFamily = fontFamily,
                            fontSize = (9 * scale).sp,
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

// --- 2. IVY LEAGUE TEMPLATE (CENTRAL CLASSIC) ---
@Composable
private fun IvyLeagueTemplate(
    data: ResumeData,
    fontFamily: FontFamily,
    scale: Float,
    spacing: Dp,
    primaryColor: Color
) {
    HeaderBlock(
        data.contactInfo,
        fontFamily = fontFamily,
        scale = scale,
        primaryColor = Color.Black,
        alignment = Alignment.CenterHorizontally,
        isSerif = true
    )
    Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
    
    if (data.summary.isNotBlank()) {
        Text(
            text = data.summary,
            fontFamily = fontFamily,
            fontSize = (11 * scale).sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(spacing))
    }
    
    // In Ivy League, experience is centralized & single column
    if (data.experiences.isNotEmpty()) {
        SectionHeading("Professional Experience", fontFamily, scale, Color.Black, hasLine = true)
        data.experiences.forEach { exp ->
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = spacing)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${exp.position} — ${exp.company}",
                        fontFamily = fontFamily,
                        fontSize = (12 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${exp.startDate} – ${if (exp.isCurrent) "Present" else exp.endDate}",
                        fontFamily = fontFamily,
                        fontSize = (10 * scale).sp,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                exp.description.split("\n").forEach { bullet ->
                    val clean = bullet.trim().removePrefix("-").trim()
                    if (clean.isNotEmpty()) {
                        Row(modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp)) {
                            Text("•", fontSize = (10 * scale).sp, color = Color.Black, modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = clean,
                                fontFamily = fontFamily,
                                fontSize = (10 * scale).sp,
                                color = Color.Black,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Projects list
    if (data.projects.isNotEmpty()) {
        SectionHeading("Selected Projects", fontFamily, scale, Color.Black)
        data.projects.forEach { proj ->
            Column(modifier = Modifier.padding(bottom = spacing)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(proj.name, fontFamily = fontFamily, fontSize = (11 * scale).sp, fontWeight = FontWeight.Bold)
                    if (proj.url.isNotBlank()) {
                        Text(proj.url, fontFamily = fontFamily, fontSize = (9 * scale).sp, fontStyle = FontStyle.Italic)
                    }
                }
                Text(proj.description, fontFamily = fontFamily, fontSize = (10 * scale).sp, color = Color.Black)
            }
        }
    }

    // Education Section
    if (data.education.isNotEmpty()) {
        SectionHeading("Education", fontFamily, scale, Color.Black, hasLine = true)
        data.education.forEach { edu ->
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = spacing)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${edu.degree}, ${edu.institution}",
                        fontFamily = fontFamily,
                        fontSize = (11 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${edu.startDate} – ${if (edu.isCurrent) "Present" else edu.endDate}",
                        fontFamily = fontFamily,
                        fontSize = (10 * scale).sp,
                        color = Color.Black
                    )
                }
                if (edu.description.isNotBlank()) {
                    Text(
                        text = edu.description,
                        fontFamily = fontFamily,
                        fontSize = (10 * scale).sp,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }
    }

    // Skills Row list
    if (data.skills.isNotEmpty()) {
        SectionHeading("Technical Skills & Certifications", fontFamily, scale, Color.Black)
        val skillsStr = data.skills.joinToString(", ") { it.name }
        Text(
            text = skillsStr,
            fontFamily = fontFamily,
            fontSize = (10 * scale).sp,
            color = Color.Black,
            lineHeight = 14.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

// --- 3. ELEGANT TEMPLATE (FLUID STYLISH) ---
@Composable
private fun ElegantTemplate(
    data: ResumeData,
    fontFamily: FontFamily,
    scale: Float,
    spacing: Dp,
    primaryColor: Color,
    accentColor: Color
) {
    // Elegant features a left accent top bar
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            HeaderBlock(data.contactInfo, fontFamily, scale, primaryColor)
        }
    }

    if (data.summary.isNotBlank()) {
        Text(
            text = data.summary,
            fontFamily = fontFamily,
            fontSize = (11 * scale).sp,
            color = Color(0xFF424242),
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(spacing))
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (data.experiences.isNotEmpty()) {
            SectionHeading("Experience Profile", fontFamily, scale, primaryColor)
            data.experiences.forEach { exp ->
                Column(modifier = Modifier.padding(bottom = spacing)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(exp.position, fontFamily = fontFamily, fontSize = (12 * scale).sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Text("${exp.startDate} – ${if (exp.isCurrent) "Present" else exp.endDate}", fontFamily = fontFamily, fontSize = (9 * scale).sp, color = Color.Gray)
                    }
                    Text(exp.company, fontFamily = fontFamily, fontSize = (10 * scale).sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Spacer(modifier = Modifier.height(2.dp))
                    exp.description.split("\n").forEach { b ->
                        val text = b.trim().removePrefix("-").trim()
                        if (text.isNotEmpty()) {
                            Row(modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)) {
                                Text("—", color = accentColor, fontSize = (10 * scale).sp, modifier = Modifier.padding(end = 6.dp))
                                Text(text, fontFamily = fontFamily, fontSize = (10 * scale).sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }

        if (data.skills.isNotEmpty()) {
            SectionHeading("Expertise Matrix", fontFamily, scale, primaryColor)
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val chunks = data.skills.chunked(3)
                chunks.forEach { grp ->
                    Column(modifier = Modifier.weight(1f)) {
                        grp.forEach { sk ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(accentColor))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(sk.name, fontFamily = fontFamily, fontSize = (10 * scale).sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        if (data.education.isNotEmpty()) {
            SectionHeading("Education", fontFamily, scale, primaryColor)
            data.education.forEach { edu ->
                Column(modifier = Modifier.padding(bottom = spacing)) {
                    Text(edu.degree, fontFamily = fontFamily, fontSize = (11 * scale).sp, fontWeight = FontWeight.Bold)
                    Text("${edu.institution}  |  ${edu.startDate} - ${edu.endDate}", fontFamily = fontFamily, fontSize = (9 * scale).sp, color = Color.Gray)
                }
            }
        }
    }
}

// --- 4. MINIMAL TEMPLATE (AMPLIFIED MINIMALISM) ---
@Composable
private fun MinimalTemplate(
    data: ResumeData,
    fontFamily: FontFamily,
    scale: Float,
    spacing: Dp,
    primaryColor: Color
) {
    HeaderBlock(data.contactInfo, fontFamily, scale, Color.Black)
    Spacer(modifier = Modifier.height(10.dp))
    
    if (data.summary.isNotBlank()) {
        Text(
            text = data.summary,
            fontFamily = fontFamily,
            fontSize = (10.5 * scale).sp,
            color = Color.DarkGray,
            lineHeight = 15.sp,
            fontStyle = FontStyle.Italic
        )
        Spacer(modifier = Modifier.height(spacing))
    }

    if (data.experiences.isNotEmpty()) {
        SectionHeading("Experiences", fontFamily, scale, Color.Black, hasLine = false)
        data.experiences.forEach { exp ->
            Column(modifier = Modifier.padding(bottom = spacing)) {
                Text(exp.company.uppercase(), fontFamily = fontFamily, fontSize = (10 * scale).sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(exp.position, fontFamily = fontFamily, fontSize = (11 * scale).sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Text("${exp.startDate} - ${if (exp.isCurrent) "Present" else exp.endDate}", fontFamily = fontFamily, fontSize = (9 * scale).sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(2.dp))
                exp.description.split("\n").forEach { b ->
                    val text = b.trim().removePrefix("-").trim()
                    if (text.isNotEmpty()) {
                        Text(
                            text = "• $text",
                            fontFamily = fontFamily,
                            fontSize = (9.5 * scale).sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(start = 4.dp, bottom = 1.dp)
                        )
                    }
                }
            }
        }
    }

    if (data.education.isNotEmpty()) {
        SectionHeading("Education", fontFamily, scale, Color.Black, hasLine = false)
        data.education.forEach { edu ->
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(edu.institution, fontFamily = fontFamily, fontSize = (10 * scale).sp, fontWeight = FontWeight.Bold)
                    Text(edu.degree, fontFamily = fontFamily, fontSize = (9.5 * scale).sp, color = Color.DarkGray)
                }
                Text("${edu.startDate} - ${edu.endDate}", fontFamily = fontFamily, fontSize = (9 * scale).sp, color = Color.Gray)
            }
        }
    }

    if (data.skills.isNotEmpty()) {
        SectionHeading("Core Competencies", fontFamily, scale, Color.Black, hasLine = false)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = data.skills.joinToString("  /  ") { it.name },
            fontFamily = fontFamily,
            fontSize = (10 * scale).sp,
            color = Color.Black,
            lineHeight = 14.sp
        )
    }
}
