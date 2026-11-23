package com.milas.rageshield.adapters

import com.milas.rageshield.core.PlatformAdapter
import android.view.accessibility.AccessibilityNodeInfo

import com.milas.rageshield.util.AccessibilityUtils

class InstagramAdapter : PlatformAdapter {
    override val supportedPackageNames = setOf("com.instagram.android")
    override val platformName = "Instagram"
    override val isStable = false

    override fun extractContent(rootNode: AccessibilityNodeInfo): String? = AccessibilityUtils.findText(rootNode)
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
