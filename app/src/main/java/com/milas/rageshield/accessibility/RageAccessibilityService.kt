package com.milas.rageshield.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.milas.rageshield.core.DecisionEngine
import com.milas.rageshield.core.PlatformRegistry
import com.milas.rageshield.core.RageDetectionEngine
import java.lang.ref.WeakReference

class RageAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = WeakReference(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val packageName = event.packageName?.toString() ?: return

        if (packageName == FACEBOOK_PACKAGE) {
            getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_LAST_FACEBOOK_EVENT, System.currentTimeMillis())
                .apply()
        }

        val adapter = PlatformRegistry.getAdapterForPackage(packageName) ?: return

        // Use the event's source node (if available) to extract content
        val rootNode = event.source ?: return
        val content = adapter.extractContent(rootNode) ?: return

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
            // Perform action (e.g., scroll away)
            performTestSwipe()
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
        const val KEY_LAST_FACEBOOK_EVENT = "last_facebook_event"

        @Volatile
        private var instance: WeakReference<RageAccessibilityService>? = null

        fun requestTestSwipe(): Boolean {
            return instance?.get()?.performTestSwipe() ?: false
        }

        fun isConnected(): Boolean = instance?.get() != null
    }
}
