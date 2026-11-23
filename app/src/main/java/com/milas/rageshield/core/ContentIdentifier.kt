package com.milas.rageshield.core

import java.security.MessageDigest

/**
 * Handles deterministic identification of content.
 */
object ContentIdentifier {

    /**
     * Creates a deterministic fingerprint from a content string.
     * Normalizes text (lowercase, trimming) before hashing.
     */
    fun generateTextFingerprint(content: String): String {
        val normalized = content.lowercase().trim()
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(normalized.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }.take(32)
    }
}
