package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.dao.GpuInsightDao
import com.example.data.model.AuditEvent
import com.example.data.model.GpuMetric

@Database(
    entities     = [GpuMetric::class, AuditEvent::class],
    version      = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gpuInsightDao(): GpuInsightDao

    companion object {
        const val NAME = "gpu_insight.db"
    }
}
