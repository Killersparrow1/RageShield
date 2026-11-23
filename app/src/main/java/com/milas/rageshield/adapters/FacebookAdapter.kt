package com.milas.rageshield.adapters

import com.milas.rageshield.core.PlatformAdapter

class FacebookAdapter : PlatformAdapter {
    override val supportedPackageNames = setOf("com.facebook.katana", "com.facebook.lite")
    override val platformName = "Facebook"
    override val isStable = true

    override fun extractContent(rootNode: android.view.accessibility.AccessibilityNodeInfo): String? {
        // Facebook uses a specific structure. Use the util to find the post body.
        return com.milas.rageshield.util.AccessibilityUtils.findText(rootNode)
    }
}
