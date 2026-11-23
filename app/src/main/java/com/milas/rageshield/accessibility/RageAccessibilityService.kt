package com.milas.rageshield.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.milas.rageshield.core.ContentIdentifier
import com.milas.rageshield.core.DecisionEngine
import com.milas.rageshield.core.FilterListRepository
import com.milas.rageshield.core.PlatformRegistry
import com.milas.rageshield.core.RageDetectionEngine
import java.lang.ref.WeakReference

class RageAccessibilityService : AccessibilityService() {

    private var lastSwipeAt = 0L
    private var lastProcessedAt = 0L
    private var lastFingerprint: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = WeakReference(this)
        FilterListRepository.init(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val packageName = event.packageName?.toString() ?: return

        val adapter = PlatformRegistry.getAdapterForPackage(packageName) ?: return

        // Content-changed events can fire many times per second; read the screen at most
        // a few times a second.
        val now = System.currentTimeMillis()
        if (now - lastProcessedAt < PROCESS_THROTTLE_MS) return
        lastProcessedAt = now

        getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putLong(statusKey(packageName), now)
            .apply()

        // Prefer the whole active window; fall back to the event source.
        val rootNode = rootInActiveWindow ?: event.source
        if (rootNode == null) {
            recordAnalysis(adapter.platformName, "Could not read the screen")
            return
        }
        val content = adapter.extractContent(rootNode)
        if (content == null) {
            recordAnalysis(adapter.platformName, "No readable caption text on screen")
            return
        }

        // Ignore repeated events for content that was already analysed.
        val fingerprint = ContentIdentifier.generateTextFingerprint(content)
        if (fingerprint == lastFingerprint) return
        lastFingerprint = fingerprint

        processContent(adapter.platformName, content)
    }

    /** Stores a one-line summary of the latest analysis (never the post text itself). */
    private fun recordAnalysis(platform: String, summary: String) {
        getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_ANALYSIS, "$platform: $summary")
            .putLong(KEY_LAST_ANALYSIS_AT, System.currentTimeMillis())
            .apply()
    }

    private fun processContent(platform: String, text: String) {
        val localDetection = RageDetectionEngine.analyze(text)

        // TODO: Query community database for hash match (Phase 4)
        val communityMatch = false

        val decision = DecisionEngine.decide(
            text,
            communityMatch,
            localDetection.score
        )

        var outcome = decision.name
        if (decision == DecisionEngine.Decision.SKIP) {
            // Cooldown so the swipe's own scroll events cannot chain into a skip loop.
            val now = System.currentTimeMillis()
            if (now - lastSwipeAt >= SWIPE_COOLDOWN_MS) {
                lastSwipeAt = now
                outcome = if (performTestSwipe()) "SKIP, swiped" else "SKIP, swipe failed"
            } else {
                outcome = "SKIP, waiting (cooldown)"
            }
        }

        val matchText = if (localDetection.matches.isEmpty()) "none"
        else localDetection.matches.take(5).joinToString(", ")
        recordAnalysis(
            platform,
            "${text.length} chars read, score ${localDetection.score}, matched: $matchText, result: $outcome"
        )
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
        private const val SWIPE_COOLDOWN_MS = 1500L
        private const val PROCESS_THROTTLE_MS = 700L
        const val KEY_LAST_ANALYSIS = "last_analysis"
        const val KEY_LAST_ANALYSIS_AT = "last_analysis_at"

        /** SharedPreferences key holding the last event time for a given app package. */
        fun statusKey(packageName: String) = "last_event_$packageName"

        @Volatile
        private var instance: WeakReference<RageAccessibilityService>? = null

        fun requestTestSwipe(): Boolean {
            return instance?.get()?.performTestSwipe() ?: false
        }

        fun isConnected(): Boolean = instance?.get() != null
    }
}
