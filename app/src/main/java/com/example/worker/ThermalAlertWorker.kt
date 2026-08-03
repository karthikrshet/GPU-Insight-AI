package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.data.AppDatabase
import com.example.data.model.AuditLogEntity
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class ThermalAlertWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(appContext)
            val dao = db.gpuInsightDao()

            val gpus = dao.getAllGpus().first()
            val alertRules = dao.getAllAlertRules().first()

            for (gpu in gpus) {
                val latestTelemetryList = dao.getLatestTelemetryForGpu(gpu.id).first()
                val latest = latestTelemetryList.firstOrNull() ?: continue

                // Check thermal threshold
                if (latest.tempC >= 80) {
                    val timestamp = System.currentTimeMillis()

                    // Save alert trigger log to Room Database
                    dao.insertAuditLog(
                        AuditLogEntity(
                            timestamp = timestamp,
                            actor = "WORK_MANAGER_DAEMON",
                            role = "BACKGROUND_WORKER",
                            action = "ALERT_TRIGGER",
                            targetResource = "${gpu.name} (${gpu.id})",
                            details = "Thermal alert: Temperature reached ${latest.tempC}°C (Exceeds 80°C threshold). Power: ${latest.powerW.toInt()}W.",
                            ipAddress = "127.0.0.1",
                            status = "SUCCESS"
                        )
                    )

                    // Mark alert rules as triggered
                    for (rule in alertRules) {
                        if (rule.enabled && rule.metricType == "TEMPERATURE" && latest.tempC >= rule.thresholdValue) {
                            dao.setAlertRuleTriggered(rule.id, true, timestamp)
                        }
                    }

                    // Post Android Notification
                    sendThermalNotification(gpu.name, latest.tempC)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun sendThermalNotification(gpuName: String, tempC: Int) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "gpu_thermal_alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "GPU Thermal Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when GPU thermal thresholds are exceeded"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("🔥 GPU Thermal Alert: $tempC°C")
            .setContentText("$gpuName exceeded safe temperature limits! Saved event to Room DB.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(gpuName.hashCode(), notification)
    }

    companion object {
        const val WORK_NAME = "GpuThermalAlertWorker"

        fun schedulePeriodicCheck(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<ThermalAlertWorker>(
                15, TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun runOneTimeCheck(context: Context) {
            val oneTimeRequest = OneTimeWorkRequestBuilder<ThermalAlertWorker>()
                .build()

            WorkManager.getInstance(context).enqueue(oneTimeRequest)
        }
    }
}
