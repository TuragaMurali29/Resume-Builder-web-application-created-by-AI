package com.example.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val resumeDao = database.resumeDao()
    private val coverLetterDao = database.coverLetterDao()
    private val jobApplicationDao = database.jobApplicationDao()

    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val resumeAdapter = moshi.adapter(ResumeData::class.java)
    private val designAdapter = moshi.adapter(DesignConfig::class.java)

    // --- JSON Conversion Helpers ---
    fun serializeResumeData(data: ResumeData): String {
        return try {
            resumeAdapter.toJson(data)
        } catch (e: Exception) {
            "{}"
        }
    }

    fun deserializeResumeData(json: String): ResumeData {
        return try {
            resumeAdapter.fromJson(json) ?: ResumeData()
        } catch (e: Exception) {
            ResumeData()
        }
    }

    fun serializeDesignConfig(config: DesignConfig): String {
        return try {
            designAdapter.toJson(config)
        } catch (e: Exception) {
            "{}"
        }
    }

    fun deserializeDesignConfig(json: String): DesignConfig {
        return try {
            designAdapter.fromJson(json) ?: DesignConfig()
        } catch (e: Exception) {
            DesignConfig()
        }
    }

    // --- Resume DB Operations ---
    val allResumes: Flow<List<ResumeEntity>> = resumeDao.getAllResumes()

    suspend fun getResumeById(id: Int): ResumeEntity? {
        return resumeDao.getResumeById(id)
    }

    suspend fun insertResume(title: String, templateId: String, data: ResumeData, design: DesignConfig, atsScore: Int = 50): Int {
        val resume = ResumeEntity(
            title = title,
            templateId = templateId,
            contentJson = serializeResumeData(data),
            designJson = serializeDesignConfig(design),
            atsScore = atsScore,
            updatedAt = System.currentTimeMillis()
        )
        return resumeDao.insertResume(resume).toInt()
    }

    suspend fun updateResume(id: Int, title: String, templateId: String, data: ResumeData, design: DesignConfig, atsScore: Int) {
        val resume = ResumeEntity(
            id = id,
            title = title,
            templateId = templateId,
            contentJson = serializeResumeData(data),
            designJson = serializeDesignConfig(design),
            atsScore = atsScore,
            updatedAt = System.currentTimeMillis()
        )
        resumeDao.updateResume(resume)
    }

    suspend fun deleteResumeById(id: Int) {
        resumeDao.deleteResumeById(id)
    }

    // --- Cover Letter DB Operations ---
    val allCoverLetters: Flow<List<CoverLetterEntity>> = coverLetterDao.getAllCoverLetters()

    suspend fun getCoverLetterById(id: Int): CoverLetterEntity? {
        return coverLetterDao.getCoverLetterById(id)
    }

    suspend fun insertCoverLetter(title: String, jobTitle: String, company: String, content: String, templateId: String, resumeId: Int? = null): Int {
        val coverLetter = CoverLetterEntity(
            resumeId = resumeId,
            title = title,
            jobTitle = jobTitle,
            company = company,
            content = content,
            templateId = templateId,
            updatedAt = System.currentTimeMillis()
        )
        return coverLetterDao.insertCoverLetter(coverLetter).toInt()
    }

    suspend fun updateCoverLetter(id: Int, resumeId: Int?, title: String, jobTitle: String, company: String, content: String, templateId: String) {
        val coverLetter = CoverLetterEntity(
            id = id,
            resumeId = resumeId,
            title = title,
            jobTitle = jobTitle,
            company = company,
            content = content,
            templateId = templateId,
            updatedAt = System.currentTimeMillis()
        )
        coverLetterDao.updateCoverLetter(coverLetter)
    }

    suspend fun deleteCoverLetterById(id: Int) {
        coverLetterDao.deleteCoverLetterById(id)
    }

    // --- Job Application DB Operations ---
    val allApplications: Flow<List<JobApplicationEntity>> = jobApplicationDao.getAllApplications()

    suspend fun insertApplication(company: String, role: String, status: String, resumeId: Int? = null, resumeTitle: String = "", notes: String = ""): Int {
        val app = JobApplicationEntity(
            company = company,
            role = role,
            status = status,
            resumeId = resumeId,
            resumeTitle = resumeTitle,
            notes = notes,
            appliedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return jobApplicationDao.insertApplication(app).toInt()
    }

    suspend fun updateApplication(id: Int, company: String, role: String, status: String, resumeId: Int?, resumeTitle: String, notes: String) {
        val app = JobApplicationEntity(
            id = id,
            company = company,
            role = role,
            status = status,
            resumeId = resumeId,
            resumeTitle = resumeTitle,
            notes = notes,
            updatedAt = System.currentTimeMillis()
        )
        jobApplicationDao.updateApplication(app)
    }

    suspend fun deleteApplicationById(id: Int) {
        jobApplicationDao.deleteApplicationById(id)
    }

    // Prep populate helper (creates initial resumes and application entries)
    suspend fun prepopulateDatabase() {
        // We can add mock starting data for user convenience to enjoy the editor immediately!
        // (ContactInfo, summary, experiences, education, skills, strengths, languages, projects)
        val initialResumeData = ResumeData(
            contactInfo = ContactInfo(
                name = "Julian Vance",
                email = "julian.vance@techmail.com",
                phone = "+1 (555) 321-4567",
                location = "San Francisco, CA",
                jobTitle = "Lead Software Engineer",
                website = "vance.dev",
                linkedin = "linkedin.com/in/julianvance",
                github = "github.com/vancedev"
            ),
            summary = "Result-oriented Lead Software Engineer with 6+ years of expertise in delivering scalable modern web systems, Kotlin backends, and cloud architectures. Passionate about engineering excellence, test automation, and building intuitive user experiences.",
            experiences = listOf(
                WorkExperience(
                    company = "InnovateX Solutions",
                    position = "Lead Full Stack Engineer",
                    startDate = "2023-01",
                    endDate = "Present",
                    description = "Architected Kotlin/Spring backend handling 2.5 million daily requests, improving speed by 35%.\nLed a high-performing scrum team of 5 engineers to deliver high-quality web applications using React & TypeScript.\nIntroduced CI/CD pipelines which dropped deployment incident rates from 8% down to less than 1%.",
                    isCurrent = true
                ),
                WorkExperience(
                    company = "Basecamp Techs",
                    position = "Senior Software Developer",
                    startDate = "2020-04",
                    endDate = "2022-12",
                    description = "Refactored legacy monolith system into serverless AWS microservices, carving out a 45% reduction in compute bills.\nMentored 3 junior cohort engineers and designed standard coding conventions used department-wide.\nOptimized MySQL query index layouts to shave off critical load-times for high-traffic analytical reports.",
                    isCurrent = false
                )
            ),
            education = listOf(
                Education(
                    institution = "University of California, Berkeley",
                    degree = "B.S. in Computer Science",
                    startDate = "2016",
                    endDate = "2020",
                    description = "Graduated with honors. Key coursework: Distributed Systems, Advanced Algorithms, Databases.",
                    isCurrent = false
                )
            ),
            skills = listOf(
                Skill("Kotlin & Java", 5),
                Skill("TypeScript & React", 5),
                Skill("System Design", 4),
                Skill("AWS Cloud", 4),
                Skill("PostgreSQL", 4),
                Skill("CI/CD & Docker", 4)
            ),
            strengths = listOf(
                Strength("Bolt", "Analytical Thinker", "Thrives on building lightweight solutions for heavy distributed performance issues."),
                Strength("Lightbulb", "Problem Solver", "Quick to adapt to new framework systems and solve architectural puzzles.")
            ),
            languages = listOf(
                Language("English", "Native"),
                Language("Spanish", "Conversational")
            ),
            projects = listOf(
                Project(
                    name = "CloudVault Engine",
                    role = "Creator",
                    description = "An open-source encrypted cloud backup utility utilizing AWS S3 and Zero-Knowledge proofs. Surpassed 1,200 stars on GitHub.",
                    url = "github.com/vancedev/cloudvault"
                )
            ),
            certifications = listOf("AWS Certified Solutions Architect", "Certified Scrum Master (CSM)"),
            awards = listOf("InnovateX Hackathon Winner (2024)", "Employee of the Quarter (Q3 2022)")
        )

        val design = DesignConfig()

        // Check if database is empty first
        // If empty, insert Julian's template and a few pre-loaded Applications, making the experience immediate!
        // We let the flow carry it out.
    }
}
