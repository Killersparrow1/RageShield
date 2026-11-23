package com.milas.rageshield.adapters

import com.milas.rageshield.core.PlatformAdapter
import android.view.accessibility.AccessibilityNodeInfo

import com.milas.rageshield.util.AccessibilityUtils

class InstagramAdapter : PlatformAdapter {
    override val supportedPackageNames = setOf("com.instagram.android")
    override val platformName = "Instagram"
    override val isStable = false

    /**
     * Instagram's first text node is usually a username or tab label, so gather all
     * visible text and keep caption-like strings (multi-word, not tiny UI labels).
     * View IDs are deliberately not used because Instagram changes them often.
     */
    override fun extractContent(rootNode: AccessibilityNodeInfo): String? {
        val captionLike = AccessibilityUtils.collectText(rootNode)
            .filter { it.length >= 12 && it.contains(' ') }
        if (captionLike.isEmpty()) return null
        return captionLike.joinToString(" ").take(MAX_CHARS)
    }

    private companion object {
        const val MAX_CHARS = 2000
    }
}

class YoutubeAdapter : PlatformAdapter {
    override val supportedPackageNames = setOf("com.google.android.youtube")
    override val platformName = "YouTube"
    override val isStable = false

    override fun extractContent(rootNode: AccessibilityNodeInfo): String? = AccessibilityUtils.findText(rootNode)
}

class TiktokAdapter : PlatformAdapter {
    override val supportedPackageNames = setOf("com.ss.android.ugc.trill", "com.zhiliaoapp.musically")
    override val platformName = "TikTok"
    override val isStable = false

    override fun extractContent(rootNode: AccessibilityNodeInfo): String? = AccessibilityUtils.findText(rootNode)
}

class TwitterAdapter : PlatformAdapter {
    override val supportedPackageNames = setOf("com.twitter.android")
    override val platformName = "X / Twitter"
    override val isStable = false

    override fun extractContent(rootNode: AccessibilityNodeInfo): String? = AccessibilityUtils.findText(rootNode)
}

class ThreadsAdapter : PlatformAdapter {
    override val supportedPackageNames = setOf("com.instagram.barcelona")
    override val platformName = "Threads"
    override val isStable = false

    override fun extractContent(rootNode: AccessibilityNodeInfo): String? = AccessibilityUtils.findText(rootNode)
}

class RedditAdapter : PlatformAdapter {
    override val supportedPackageNames = setOf("com.reddit.frontpage")
    override val platformName = "Reddit"
    override val isStable = false

    override fun extractContent(rootNode: AccessibilityNodeInfo): String? = AccessibilityUtils.findText(rootNode)
}
