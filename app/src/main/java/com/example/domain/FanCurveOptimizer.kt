package com.example.domain

enum class FanMode { DEFAULT, SILENT, PERFORMANCE }

data class FanCurvePoint(val tempC: Int, val fanPercent: Int)

object FanCurveOptimizer {
    private val DEFAULT = listOf(
        FanCurvePoint(30,0), FanCurvePoint(50,30), FanCurvePoint(60,50),
        FanCurvePoint(70,65), FanCurvePoint(80,80), FanCurvePoint(90,100))
    private val SILENT = listOf(
        FanCurvePoint(30,0), FanCurvePoint(60,20), FanCurvePoint(70,40),
        FanCurvePoint(80,65), FanCurvePoint(90,100))
    private val PERFORMANCE = listOf(
        FanCurvePoint(30,40), FanCurvePoint(50,60), FanCurvePoint(60,75),
        FanCurvePoint(70,90), FanCurvePoint(80,100))

    fun getCurve(mode: FanMode): List<FanCurvePoint> = when (mode) {
        FanMode.SILENT      -> SILENT
        FanMode.PERFORMANCE -> PERFORMANCE
        FanMode.DEFAULT     -> DEFAULT
    }

    fun interpolate(tempC: Float, curve: List<FanCurvePoint>): Int {
        val sorted = curve.sortedBy { it.tempC }
        val lo = sorted.lastOrNull  { it.tempC <= tempC } ?: return sorted.first().fanPercent
        val hi = sorted.firstOrNull { it.tempC >  tempC } ?: return sorted.last().fanPercent
        val r = (tempC - lo.tempC) / (hi.tempC - lo.tempC)
        return (lo.fanPercent + r * (hi.fanPercent - lo.fanPercent)).toInt()
    }
}
