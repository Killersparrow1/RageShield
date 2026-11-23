package com.milas.rageshield.adapters

import android.view.accessibility.AccessibilityNodeInfo
import com.milas.rageshield.core.PlatformAdapter
import com.milas.rageshield.util.AccessibilityUtils

/**
 * Shared base for feed-style social apps. Reads caption-like text from the visible
 * hierarchy instead of relying on view IDs, which apps change often.
 *
 * Add a new platform by subclassing this, registering it in PlatformRegistry and
 * adding its package to res/xml/rageshield_accessibility_service.xml.
 */
abstract class FeedAdapter(
    override val platformName: String,
    override val supportedPackageNames: Set<String>,
    override val isStable: Boolean = false
) : PlatformAdapter {

    override fun extractContent(rootNode: AccessibilityNodeInfo): String? =
        AccessibilityUtils.captionLikeText(rootNode)
}
