package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Gemini Pro API for GPU error analysis.
 * Key injected via Hilt @Named from BuildConfig — never hardcoded.
 * Stack traces sanitized before transmission (AWS keys, Bearer tokens, IPs).
 */
@Singleton
class GeminiApiService @Inject constructor(
    private val httpClient: OkHttpClient,
    @Named("gemini_api_key") private val apiKey: String
) {
    private val endpoint =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"

    suspend fun analyzeGpuError(stackTrace: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                require(apiKey.isNotBlank()) {
                    "Gemini API key not configured. Set GEMINI_API_KEY in local.properties."
                }
                val body = buildBody(redactSecrets(stackTrace))
                val rsp  = httpClient.newCall(Request.Builder()
                    .url("$endpoint?key=$apiKey")
                    .post(body.toRequestBody("application/json".toMediaType()))
                    .build()).execute()
                if (!rsp.isSuccessful) throw Exception("Gemini HTTP ${rsp.code}: ${rsp.message}")
                parseResponse(rsp.body?.string() ?: throw Exception("Empty body"))
            }
        }

    private fun buildBody(prompt: String) = JSONObject().apply {
        put("contents", org.json.JSONArray().apply {
            put(JSONObject().put("parts", org.json.JSONArray().apply {
                put(JSONObject().put("text", "GPU Error Analysis:\n$prompt"))
            }))
        })
    }.toString()

    private fun parseResponse(json: String): String = try {
        JSONObject(json).getJSONArray("candidates").getJSONObject(0)
            .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
    } catch (e: JSONException) { "Parse error: ${e.message}" }

    private fun redactSecrets(s: String) = s
        .replace(Regex("AKIA[A-Z0-9]{16}"), "[AWS_KEY_REDACTED]")
        .replace(Regex("Bearer [A-Za-z0-9\-._~+/]+=*"), "[TOKEN_REDACTED]")
        .replace(Regex("\b(?:10|172|192)\.\d{1,3}\.\d{1,3}\.\d{1,3}\b"), "[IP_REDACTED]")
}
