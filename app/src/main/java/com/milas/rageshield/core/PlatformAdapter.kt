package com.milas.rageshield.core

/**
 * Interface for platform-specific content handling.
 */
interface PlatformAdapter {
    val supportedPackageNames: Set<String>
    val platformName: String
    val isStable: Boolean

    fun canIdentifyContent(packageName: String): Boolean = supportedPackageNames.contains(packageName)
    
    // ... existing platform-specific metadata ...
    
    /**
     * Extracts relevant content from the platform's view hierarchy.
     */
    fun extractContent(rootNode: android.view.accessibility.AccessibilityNodeInfo): String?
}
