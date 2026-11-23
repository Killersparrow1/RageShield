package com.milas.rageshield.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
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
            getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putLong(KEY_LAST_FACEBOOK_EVENT, System.currentTimeMillis())
                .putString(KEY_LAST_PACKAGE, packageName)
                .apply()
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
        const val FACEBOOK_PACKAGE = "com.facebook.katana"
        private const val PREFS = "rageshield_status"
        const val KEY_LAST_FACEBOOK_EVENT = "last_facebook_event"
        const val KEY_LAST_PACKAGE = "last_package"

        @Volatile
        private var instance: WeakReference<RageAccessibilityService>? = null

        fun requestTestSwipe(): Boolean {
            return instance?.get()?.performTestSwipe() ?: false
        }

        fun isConnected(): Boolean = instance?.get() != null
    }
}
