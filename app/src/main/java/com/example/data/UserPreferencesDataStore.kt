package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")

class UserPreferencesDataStore(private val context: Context) {
    private object Keys {
        val THERMAL_THRESHOLD = floatPreferencesKey("thermal_threshold_c")
        val REFRESH_INTERVAL  = longPreferencesKey("refresh_interval_ms")
        val DARK_MODE         = booleanPreferencesKey("dark_mode")
    }

    private val safeData = context.dataStore.data.catch { e ->
        if (e is IOException) emit(emptyPreferences()) else throw e
    }

    val thermalThreshold: Flow<Float>  = safeData.map { it[Keys.THERMAL_THRESHOLD] ?: 85f }
    val refreshIntervalMs: Flow<Long>  = safeData.map { it[Keys.REFRESH_INTERVAL]  ?: 500L }
    val isDarkMode: Flow<Boolean>      = safeData.map { it[Keys.DARK_MODE]         ?: true }

    suspend fun setThermalThreshold(v: Float) {
        context.dataStore.edit { it[Keys.THERMAL_THRESHOLD] = v.coerceIn(50f, 100f) }
    }

    suspend fun setRefreshInterval(ms: Long) {
        context.dataStore.edit { it[Keys.REFRESH_INTERVAL] = ms.coerceIn(250L, 5000L) }
    }
}
