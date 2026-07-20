package com.example.ui.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

object GpuNotificationManager {
    const val THERMAL_CHANNEL  = "thermal_alerts"
    const val OOM_CHANNEL      = "oom_alerts"
    const val HEALTH_CHANNEL   = "health_summary"

    fun createChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        listOf(
            NotificationChannel(THERMAL_CHANNEL, "Thermal Alerts",
                NotificationManager.IMPORTANCE_HIGH).apply {
                description = "GPU temperature threshold exceeded"
            },
            NotificationChannel(OOM_CHANNEL, "OOM Warnings",
                NotificationManager.IMPORTANCE_HIGH).apply {
                description = "GPU out-of-memory events"
            },
            NotificationChannel(HEALTH_CHANNEL, "Health Summary",
                NotificationManager.IMPORTANCE_LOW)
        ).forEach { nm.createNotificationChannel(it) }
    }

    fun buildThermalAlert(context: Context, gpuName: String, tempC: Float) =
        NotificationCompat.Builder(context, THERMAL_CHANNEL)
            .setContentTitle("Thermal Alert: $gpuName")
            .setContentText("Temperature: ${tempC.toInt()}C — above threshold!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
}
