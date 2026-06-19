package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@androidx.room.Entity(tableName = "resumes")
data class ResumeEntity(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val templateId: String,
    val contentJson: String, // Serialized ResumeData
    val designJson: String,  // Serialized DesignConfig
    val atsScore: Int = 50,
    val updatedAt: Long = System.currentTimeMillis()
)

@androidx.room.Entity(tableName = "cover_letters")
data class CoverLetterEntity(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Int = 0,
    val resumeId: Int? = null,
    val title: String,
    val jobTitle: String,
    val company: String,
    val content: String,
    val templateId: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@androidx.room.Entity(tableName = "job_applications")
data class JobApplicationEntity(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Int = 0,
    val company: String,
    val role: String,
    val status: String, // Wishlist, Applied, Interview, Offer, Rejected
    val resumeId: Int? = null,
    val resumeTitle: String = "",
    val notes: String = "",
    val appliedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes ORDER BY updatedAt DESC")
    fun getAllResumes(): Flow<List<ResumeEntity>>

    @Query("SELECT * FROM resumes WHERE id = :id LIMIT 1")
    suspend fun getResumeById(id: Int): ResumeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResume(resume: ResumeEntity): Long

    @Update
    suspend fun updateResume(resume: ResumeEntity)

    @Delete
    suspend fun deleteResume(resume: ResumeEntity)

    @Query("DELETE FROM resumes WHERE id = :id")
    suspend fun deleteResumeById(id: Int)
}

@Dao
interface CoverLetterDao {
    @Query("SELECT * FROM cover_letters ORDER BY updatedAt DESC")
    fun getAllCoverLetters(): Flow<List<CoverLetterEntity>>

    @Query("SELECT * FROM cover_letters WHERE id = :id LIMIT 1")
    suspend fun getCoverLetterById(id: Int): CoverLetterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoverLetter(coverLetter: CoverLetterEntity): Long

    @Update
    suspend fun updateCoverLetter(coverLetter: CoverLetterEntity)

    @Query("DELETE FROM cover_letters WHERE id = :id")
    suspend fun deleteCoverLetterById(id: Int)
}

@Dao
interface JobApplicationDao {
    @Query("SELECT * FROM job_applications ORDER BY updatedAt DESC")
    fun getAllApplications(): Flow<List<JobApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: JobApplicationEntity): Long

    @Update
    suspend fun updateApplication(application: JobApplicationEntity)

    @Query("DELETE FROM job_applications WHERE id = :id")
    suspend fun deleteApplicationById(id: Int)
}

@Database(
    entities = [ResumeEntity::class, CoverLetterEntity::class, JobApplicationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resumeDao(): ResumeDao
    abstract fun coverLetterDao(): CoverLetterDao
    abstract fun jobApplicationDao(): JobApplicationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "resume_ai_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
