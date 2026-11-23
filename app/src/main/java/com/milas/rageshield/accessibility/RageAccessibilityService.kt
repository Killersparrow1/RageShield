package com.milas.rageshield.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.milas.rageshield.core.ContentIdentifier
import com.milas.rageshield.core.DecisionEngine
import com.milas.rageshield.core.PlatformRegistry
import com.milas.rageshield.core.RageDetectionEngine
import java.lang.ref.WeakReference

class RageAccessibilityService : AccessibilityService() {

    private var lastSwipeAt = 0L
    private var lastFingerprint: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = WeakReference(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val packageName = event.packageName?.toString() ?: return

        val statusKey = when (packageName) {
            FACEBOOK_PACKAGE -> KEY_LAST_FACEBOOK_EVENT
            INSTAGRAM_PACKAGE -> KEY_LAST_INSTAGRAM_EVENT
            else -> null
        }
        if (statusKey != null) {
            getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putLong(statusKey, System.currentTimeMillis())
                .apply()
        }

        val adapter = PlatformRegistry.getAdapterForPackage(packageName) ?: return

        // Scroll events often carry only a small container as their source, so prefer
        // the whole active window and fall back to the event source.
        val rootNode = rootInActiveWindow ?: event.source ?: return
        val content = adapter.extractContent(rootNode) ?: return

        // Ignore repeated events for content that was already analysed.
        val fingerprint = ContentIdentifier.generateTextFingerprint(content)
        if (fingerprint == lastFingerprint) return
        lastFingerprint = fingerprint

        processContent(content)
    }

    private fun processContent(text: String) {
        val localDetection = RageDetectionEngine.analyze(text)

        // TODO: Query community database for hash match (Phase 4)
        val communityMatch = false

        val decision = DecisionEngine.decide(
            text,
            communityMatch,
            localDetection.score
        )

        if (decision == DecisionEngine.Decision.SKIP) {
            // Cooldown so the swipe's own scroll events cannot chain into a skip loop.
            val now = System.currentTimeMillis()
            if (now - lastSwipeAt >= SWIPE_COOLDOWN_MS) {
                lastSwipeAt = now
                performTestSwipe()
            }
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        if (instance?.get() === this) instance = null
        super.onDestroy()
    }

    /**
     * Sends a vertical swipe gesture. Triggered manually from the UI, and also
     * by processContent when DecisionEngine returns SKIP.
     */
    fun performTestSwipe(): Boolean {
        val width = resources.displayMetrics.widthPixels.toFloat()
        val height = resources.displayMetrics.heightPixels.toFloat()

        val path = Path().apply {
            moveTo(width * 0.50f, height * 0.78f)
            lineTo(width * 0.50f, height * 0.30f)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0L, 350L)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        return dispatchGesture(gesture, null, null)
    }

    companion object {
        private const val PREFS = "rageshield_status"
        private const val FACEBOOK_PACKAGE = "com.facebook.katana"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
        private const val SWIPE_COOLDOWN_MS = 1500L
        const val KEY_LAST_FACEBOOK_EVENT = "last_facebook_event"
        const val KEY_LAST_INSTAGRAM_EVENT = "last_instagram_event"

        @Volatile
        private var instance: WeakReference<RageAccessibilityService>? = null

        fun requestTestSwipe(): Boolean {
            return instance?.get()?.performTestSwipe() ?: false
        }

        fun isConnected(): Boolean = instance?.get() != null
    }
}
