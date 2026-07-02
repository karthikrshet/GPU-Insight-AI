package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class TritonStatus(
    val isLive: Boolean, val isReady: Boolean,
    val version: String, val extensions: List<String>
)

/**
 * NVIDIA Triton Inference Server health checker using KFServing v2 HTTP API.
 */
@Singleton
class TritonHealthChecker @Inject constructor(
    private val httpClient: OkHttpClient
) {
    suspend fun check(tritonUrl: String): TritonStatus = withContext(Dispatchers.IO) {
        runCatching {
            val live  = get("$tritonUrl/v2/health/live").isNotEmpty()
            val ready = get("$tritonUrl/v2/health/ready").isNotEmpty()
            val meta  = JSONObject(get("$tritonUrl/v2"))
            TritonStatus(live, ready, meta.optString("version", "unknown"),
                meta.optJSONArray("extensions")?.let { arr ->
                    (0 until arr.length()).map { arr.getString(it) }
                } ?: emptyList())
        }.getOrDefault(TritonStatus(false, false, "unknown", emptyList()))
    }

    private fun get(url: String) =
        httpClient.newCall(Request.Builder().url(url).build()).execute().body?.string() ?: ""
}
