package com.example.data

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class ContactInfo(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val jobTitle: String = "",
    val website: String = "",
    val linkedin: String = "",
    val github: String = ""
)

@JsonClass(generateAdapter = true)
data class WorkExperience(
    val id: String = UUID.randomUUID().toString(),
    val company: String = "",
    val position: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "", // Separate bullets with newlines
    val isCurrent: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Education(
    val id: String = UUID.randomUUID().toString(),
    val institution: String = "",
    val degree: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val isCurrent: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Skill(
    val name: String = "",
    val level: Int = 3 // 1 to 5
)

@JsonClass(generateAdapter = true)
data class Strength(
    val icon: String = "Star", // Star, Bolt, Favorite, Lightbulb, Face
    val title: String = "",
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class Language(
    val name: String = "",
    val level: String = "Fluent" // Native, Fluent, Conversational
)

@JsonClass(generateAdapter = true)
data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val role: String = "",
    val description: String = "",
    val url: String = ""
)

@JsonClass(generateAdapter = true)
data class CustomSection(
    val title: String = "",
    val content: String = ""
)

@JsonClass(generateAdapter = true)
data class DesignConfig(
    val fontFamily: String = "Inter", // Inter, Serif, Monospace, Elegant
    val fontSizeMultiplier: Float = 1.0f,
    val primaryColor: String = "#2D9E72", // Default green
    val backgroundColor: String = "#F9F9F7", // Default slate/warm
    val accentColor: String = "#A396E2", // Purple
    val peachColor: String = "#FFE0B9", // Peach
    val isDoubleColumn: Boolean = false,
    val spacing: String = "Normal" // Compact, Normal, Spacious
)

@JsonClass(generateAdapter = true)
data class ResumeData(
    val contactInfo: ContactInfo = ContactInfo(),
    val summary: String = "",
    val experiences: List<WorkExperience> = emptyList(),
    val education: List<Education> = emptyList(),
    val skills: List<Skill> = emptyList(),
    val strengths: List<Strength> = emptyList(),
    val languages: List<Language> = emptyList(),
    val projects: List<Project> = emptyList(),
    val certifications: List<String> = emptyList(),
    val awards: List<String> = emptyList(),
    val customSections: List<CustomSection> = emptyList()
)
