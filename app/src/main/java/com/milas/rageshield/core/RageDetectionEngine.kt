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
    // Original rage words
    "hate", "exposed", "destroy", "scandal", "shocking", "urgent", "must watch",
    "breaking", "triggered",

    // Tamil Nadu political / rage keywords
    "tamil nadu politics",
    "tn politics",
    "tamil politics",
    "tamilnadu politics",
    "dravidian politics",
    "tamil nationalism",
    "thamizh desiyam",
    "tamil desiyam",
    "tnelection",
    "tnelection2026",
    "tamilnaduelection",
    "tamilnaduelection2026",
    "tamilnadu election 2026",
    "tn election 2026",
    "election commission of india",
    "election commission tamil nadu",
    "dmk",
    "aiadmk",
    "bjp tamil nadu",
    "bjp4tn",
    "congress tamil nadu",
    "tvk",
    "tamizhaga vettri kazhagam",
    "tamizhaga vetri kazhagam",
    "naam tamizhar katchi",
    "ntk",
    "seeman",
    "seemanism",
    "thalapathy vijay",
    "vijay2026",
    "tvk2026",
    "annamalai",
    "k annamalai",
    "edappadi palaniswami",
    "eps",
    "o panneerselvam",
    "ops",
    "opsvseps",
    "mk stalin",
    "stalin",
    "udhayanidhi stalin",
    "kanimozhi",
    "dayanidhi maran",
    "rn ravi",
    "tamil nadu governor",
    "tn governor",
    "tnpoliticalcrisis",
    "dissolvetngovt",
    "tamilnadu political crisis",
    "dmk government",
    "aiadmk government",
    "bjp government tamil nadu",
    "anti-tamil",
    "anti-hindu",
    "anti-national",
    "anti-india",
    "traitor",
    "separatist",
    "fascist",
    "dictator",
    "goondas",
    "rowdy",
    "scam",
    "corruption",
    "propaganda",
    "fake news",
    "misinformation",
    "disinformation",
    "hate speech",
    "migrant workers",
    "north indian",
    "north indians",
    "hindi imposition",
    "neet",
    "cauvery",
    "cauvery water",
    "katchatheevu",
    "fishermen issue",
    "sri lanka fishermen",
    "reservation",
    "caste politics",
    "temple politics",
    "church politics",
    "conversion",
    "love jihad",
    "drug menace",
    "sand mining",
    "liquor scam",
    "money laundering",
    "ed raid",
    "it raid",
    "arrest",
    "protest",
    "strike",
    "bandh",
    "riot",
    "violence",
    "police",
    "fir",
    "court case",
    "viral",
    "shame",
    "shameful",
    "traitors",
    "anti-tamil forces",
    "anti-hindu forces",
    "anti-national forces",
    "tamil vs dmk",
    "tamils vs dmk",
    "tamilnadu government",
    "tn government",

    // Hashtags
    "#tnelection2026",
    "#tamilnaduelection2026",
    "#tnelection",
    "#tamilpolitics",
    "#tnpolitics",
    "#politicalnews",
    "#tvk",
    "#tamizhagavettrikazhagam",
    "#thalapathyvijay",
    "#naamthamizharkatchi",
    "#seemanism",
    "#bjptamilnadu",
    "#dmk",
    "#aiadmk",
    "#tnpoliticalcrisis",
    "#dissolvetngovt",
    "#opsvseps",
    "#tamilnationalism",
    "#thamizhdesiyam",
    "#tamilsvsdmk",
    "#dmkgovernment",
    "#admk government",
    "#bjp4tn",
    "#vijay2026",
    "#tvk2026",
    "#tamilnadu2026",

    // Tamil script terms
    "தமிழ்நாடு அரசியல்",
    "தமிழ் அரசியல்",
    "தேர்தல்",
    "தேர்தல் ஆணையம்",
    "திமுக",
    "அதிமுக",
    "பாஜக",
    "தவெக",
    "தமிழக வெற்றி கழகம்",
    "நாம் தமிழர் கட்சி",
    "சீமான்",
    "விஜய்",
    "தளபதி விஜய்",
    "அண்ணாமலை",
    "ஸ்டாலின்",
    "எடப்பாடி",
    "ஓபிஎஸ்",
    "ஈபிஎஸ்",
    "தமிழ் தேசியம்",
    "வட இந்தியர்",
    "மொழி வெறி",
    "போலி செய்தி",
    "அவதூறு",
    "காவிரி",
    "மீன் பிடி",
    "கச்சத்தீவு",
    "இட ஒதுக்கீடு",
    "சாதி",
    "கோவில்",
    "தேசியம்",
    "ஊழல்",
    "மோசடி",
    "வன்முறை",
    "போராட்டம்"
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
        RAGE_KEYWORDS.forEach { keyword ->
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
