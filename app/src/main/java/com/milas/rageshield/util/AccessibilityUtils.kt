package com.milas.rageshield.util

import android.view.accessibility.AccessibilityNodeInfo

/**
 * Utility to recursively traverse accessibility nodes to find meaningful text.
 */
object AccessibilityUtils {

    /**
     * Traverses node tree to find the first non-empty text in children.
     * This is a generic approach to extract content.
     */
    fun findText(node: AccessibilityNodeInfo?): String? {
        if (node == null) return null
        
        // Return if this node has meaningful text
        val text = node.text
        if (!text.isNullOrBlank()) {
            return text.toString()
        }

        // Recursively search children
        for (i in 0 until node.childCount) {
            val childText = findText(node.getChild(i))
            if (!childText.isNullOrBlank()) {
                return childText
            }
        }
        return null
    }
}
