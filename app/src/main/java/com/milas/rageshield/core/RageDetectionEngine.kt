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

    private class Matcher(val keyword: String, val regex: Regex?) {
        fun matches(text: String): Boolean =
            if (regex != null) regex.containsMatchIn(text) else text.contains(keyword)
    }

    // Latin-script entries must match whole words (optionally with a plural/verb ending),
    // so short words like "eps" do not fire inside "steps". Entries in other scripts
    // (Tamil, Hindi, ...) and hashtags use plain substring matching.
    private val LATIN_ENTRY = Regex("^[a-z0-9][a-z0-9 \\-]*$")

    @Volatile
    private var matcherCache: Pair<Set<String>, List<Matcher>>? = null

    private fun matchers(): List<Matcher> {
        val current = FilterListRepository.keywords
        matcherCache?.let { if (it.first === current) return it.second }
        val built = current.map { kw ->
            if (LATIN_ENTRY.matches(kw)) {
                Matcher(
                    kw,
                    Regex("(?<![a-z0-9])" + Regex.escape(kw) + "(?:s|es|ed|d|ing)?(?![a-z0-9])")
                )
            } else {
                Matcher(kw, null)
            }
        }
        matcherCache = current to built
        return built
    }

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
        matchers().forEach { m ->
            if (m.matches(lowered)) matchCount++
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
