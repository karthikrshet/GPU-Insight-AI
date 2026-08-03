package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.GpuInsightDao
import com.example.data.model.*

@Database(
    entities = [
        ClusterNodeEntity::class,
        GpuEntity::class,
        MetricTelemetryEntity::class,
        ProcessEntity::class,
        AlertRuleEntity::class,
        AuditLogEntity::class,
        ReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gpuInsightDao(): GpuInsightDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gpu_insight_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
