package com.example.ui.util

import com.example.data.model.GpuMetric

fun GpuMetric.formatVramUsage()   = "${vramUsedMb}MB / ${vramTotalMb}MB (${vramPercentUsed()}%)"
fun GpuMetric.vramPercentUsed()   = if (vramTotalMb > 0) ((vramUsedMb.toFloat() / vramTotalMb) * 100).toInt() else 0
fun GpuMetric.formatTemperature() = "${temperatureCelsius.toInt()}°C"
fun GpuMetric.formatPower()       = "${powerDrawWatts.toInt()}W"
fun GpuMetric.formatClock()       = "${clockFrequencyMhz} MHz"
fun GpuMetric.formatUtil()        = "${utilizationPercent.toInt()}%"

fun Float.toWattsString()         = "${"%.1f".format(this)}W"
fun Long.toMbString()             = "$this MB"
fun Long.toGbString()             = "${"%.1f".format(this / 1024.0)} GB"
