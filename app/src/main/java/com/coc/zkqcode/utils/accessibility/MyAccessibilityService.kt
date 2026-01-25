package com.coc.zkqcode.utils.accessibility

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.os.Handler
import android.os.Looper

@SuppressLint("AccessibilityPolicy")
class MyAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var isDetectionEnabled = false
        private var instance: MyAccessibilityService? = null
        private val handler = Handler(Looper.getMainLooper())
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isDetectionEnabled) return

        val eventType = event?.eventType

        // Triggered when window changes, try to scan all windows
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED
        ) {

            // Scan all interactive windows
            for (window in windows) {
                val rootNode = window.root ?: continue
                if (findAndClickTarget(rootNode)) {
                    break // Exit after successful click
                }
            }

            // schedule it to be re-enabled after 10 seconds
            handler.postDelayed({
                isDetectionEnabled = true
            }, 10000)
        }
    }

    private fun findAndClickTarget(rootNode: AccessibilityNodeInfo): Boolean {
        // Priority 1: ID (android:id/button1 is the standard confirm button ID for system dialogs)
        val idNodes = rootNode.findAccessibilityNodeInfosByViewId("android:id/button1")

        // Priority 2: Text (Support Chinese and English)
        val textNodes = rootNode.findAccessibilityNodeInfosByText("立即开始")
        val enNodes = rootNode.findAccessibilityNodeInfosByText("Start now")

        val targetNode = idNodes.firstOrNull() ?: textNodes.firstOrNull() ?: enNodes.firstOrNull()

        if (targetNode != null && targetNode.isEnabled) {
            performClick(targetNode)
            // Disable detection immediately after click to avoid interfering with other operations
            isDetectionEnabled = false
            return true
        }
        return false
    }

    private fun performClick(node: AccessibilityNodeInfo) {
        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            node.parent?.let { performClick(it) }
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
