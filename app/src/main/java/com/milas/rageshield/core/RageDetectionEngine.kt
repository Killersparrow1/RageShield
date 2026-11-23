package com.milas.rageshield.core

import java.util.Locale

/**
 * On-device rage detection engine.
 *
 * Initial version uses heuristic analysis. Future versions will integrate
 * local machine learning models (e.g., TensorFlow Lite).
 */
object RageDetectionEngine {

    /** Built-in fallback words, used until (and alongside) the community list. */
    val BUILT_IN_KEYWORDS: Set<String> = setOf(
        "hate", "exposed", "destroy", "scandal", "shocking", "urgent", "must watch",
        "breaking", "triggered"
    )

    data class DetectionResult(
        val score: Int,
        val detectedLanguage: String?,
        val confidence: Double,
        val explanation: String
    )

    fun analyze(content: String): DetectionResult {
        val lowered = content.lowercase()
        val language = Locale.getDefault().language
        
        var matchCount = 0
        FilterListRepository.keywords.forEach { keyword ->
            if (lowered.contains(keyword)) matchCount++
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
