package com.milas.rageshield.core

/**
 * Orchestrates the decision to skip content based on community reports
 * and local detection.
 */
object DecisionEngine {
    
    // User configurable threshold (could be moved to Settings/Preferences)
    var rageThreshold = 90
    var isAutomaticSkipEnabled = true

    enum class Decision {
        SKIP,
        REVIEW,
        ALLOW
    }

    /**
     * Determines whether to skip the current content.
     */
    fun decide(content: String, communityMatch: Boolean, localScore: Int): Decision {
        // Priority A: Community Verified (trusted over local)
        if (communityMatch) {
            return if (isAutomaticSkipEnabled) Decision.SKIP else Decision.REVIEW
        }

        // Priority B: Local Detection
        if (localScore >= rageThreshold) {
            return if (isAutomaticSkipEnabled) Decision.SKIP else Decision.REVIEW
        }

        return Decision.ALLOW
    }
}
