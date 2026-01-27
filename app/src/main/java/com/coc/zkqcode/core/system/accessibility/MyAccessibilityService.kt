package com.coc.zkqcode.core.system.accessibility

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

        fun disableService() {
            instance?.disableSelf()
        }
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
                val found = findAndClickTarget(rootNode)
                if (found) break
            }

            // schedule it to be close after 10 seconds
            handler.postDelayed({
                isDetectionEnabled = false
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

        var result = false
        if (targetNode != null && targetNode.isEnabled) {
            performClick(targetNode)
            // Disable detection immediately after click to avoid interfering with other operations
            handler.postDelayed({//keep detecting for 1 second
                isDetectionEnabled = false
            }, 1000)
            result = true
        }

        return result
    }

    private fun performClick(node: AccessibilityNodeInfo) {
        var current: AccessibilityNodeInfo? = node
        while (current != null) {
            if (current.isClickable) {
                current.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return
            }
            current = current.parent
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
