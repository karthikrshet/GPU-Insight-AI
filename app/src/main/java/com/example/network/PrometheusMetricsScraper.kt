package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrometheusMetricsScraper @Inject constructor(
    private val httpClient: OkHttpClient
) {
    suspend fun scrapeMetric(prometheusUrl: String, query: String): String =
        withContext(Dispatchers.IO) {
            runCatching {
                val url = "$prometheusUrl/api/v1/query?query=$query"
                val request = Request.Builder().url(url).build()
                httpClient.newCall(request).execute().body?.string() ?: ""
            }.getOrDefault("")
        }

    suspend fun queryRange(
        prometheusUrl: String, metric: String,
        startUnix: Long, endUnix: Long, stepSecs: Int = 30
    ): String = withContext(Dispatchers.IO) {
        runCatching {
            val url = "$prometheusUrl/api/v1/query_range?query=$metric" +
                      "&start=$startUnix&end=$endUnix&step=${stepSecs}s"
            val request = Request.Builder().url(url).build()
            httpClient.newCall(request).execute().body?.string() ?: ""
        }.getOrDefault("")
    }
}
