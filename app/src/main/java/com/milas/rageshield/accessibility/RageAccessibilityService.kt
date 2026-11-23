package com.milas.rageshield.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.milas.rageshield.core.DecisionEngine
import com.milas.rageshield.core.PlatformRegistry
import com.milas.rageshield.core.RageDetectionEngine

class RageAccessibilityService : AccessibilityService() {
    // ... (existing members)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val packageName = event.packageName?.toString() ?: return
        
        val adapter = PlatformRegistry.getAdapterForPackage(packageName) ?: return
        
        // Use the event's source node (if available) to extract content
        val rootNode = event.source ?: return
        val content = adapter.extractContent(rootNode) ?: return
        
        processContent(content, packageName)
    }

    // Example of how the DecisionEngine integrates into the event flow
    private fun processContent(text: String, packageName: String) {
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
     * Deliberately user-triggered in v0.1. No automatic swiping occurs
     * just because Facebook is opened or a scroll event is received.
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
        const val KEY_LAST_EVENT = "last_event"
        const val KEY_LAST_PACKAGE = "last_package"

        @Volatile
        private var instance: WeakReference<RageAccessibilityService>? = null

        fun requestTestSwipe(): Boolean {
            return instance?.get()?.performTestSwipe() ?: false
        }

        fun isConnected(): Boolean = instance?.get() != null
    }
}
