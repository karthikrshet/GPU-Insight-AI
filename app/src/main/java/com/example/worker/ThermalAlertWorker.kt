package com.example.worker

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class ThermalAlertWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if (runAttemptCount >= 3) return@withContext Result.failure()
            val threshold = inputData.getFloat("thermal_threshold_c", 85f)
            val temp = pollGpuTemperature()
            if (temp > threshold) notifyThermalExceeded(temp, threshold)
            Result.success(workDataOf("last_temp_c" to temp))
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private fun pollGpuTemperature() = (60..95).random().toFloat()
    private fun notifyThermalExceeded(t: Float, th: Float) { /* NotificationManager */ }

    companion object {
        const val WORK_TAG = "thermal_monitor"
        fun build(thresholdC: Float = 85f): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<ThermalAlertWorker>(15, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .setInputData(workDataOf("thermal_threshold_c" to thresholdC))
                .addTag(WORK_TAG)
                .build()
    }
}
