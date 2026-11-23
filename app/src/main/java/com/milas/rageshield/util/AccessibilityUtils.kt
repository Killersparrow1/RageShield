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

    /**
     * Collects all non-blank text in the node tree, in traversal order.
     * Capped by [maxNodes] so a huge hierarchy cannot stall the service.
     */
    fun collectText(root: AccessibilityNodeInfo?, maxNodes: Int = 300): List<String> {
        if (root == null) return emptyList()
        val out = ArrayList<String>()
        var visited = 0

        fun walk(node: AccessibilityNodeInfo?) {
            if (node == null || visited >= maxNodes) return
            visited++
            val text = node.text
            if (!text.isNullOrBlank()) out.add(text.toString().trim())
            for (i in 0 until node.childCount) {
                if (visited >= maxNodes) return
                walk(node.getChild(i))
            }
        }

        walk(root)
        return out
    }
}
