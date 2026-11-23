package com.milas.rageshield.core

import java.util.Locale

/**
 * On-device rage detection engine.
 *
 * Initial version uses heuristic analysis. Future versions will integrate
 * local machine learning models (e.g., TensorFlow Lite).
 */
object RageDetectionEngine {

    // Keywords or patterns that often indicate rage-farming, to be expanded.
    private val RAGE_KEYWORDS = setOf(
        "hate", "exposed", "destroy", "scandal", "shocking", "urgent", "must watch",
        "exposed", "breaking", "triggered"
    )

    data class DetectionResult(
        val score: Int,
        val detectedLanguage: String?,
        val confidence: Double,
        val explanation: String
    )

    fun analyze(content: String): DetectionResult {
        val words = content.lowercase().split(Regex("\\s+"))
        val language = Locale.getDefault().language
        
        var matchCount = 0
        RAGE_KEYWORDS.forEach { keyword ->
            if (content.contains(keyword)) matchCount++
        }

        // Simple heuristic: scale match count to a score out of 100
        val score = (matchCount * 20).coerceIn(0, 100)
        
        return DetectionResult(
            score = score,
            detectedLanguage = language,
            confidence = if (score > 50) 0.8 else 0.5,
            explanation = if (score > 70) "High frequency of provocative patterns." else "Content appears neutral."
        )
    }
}
