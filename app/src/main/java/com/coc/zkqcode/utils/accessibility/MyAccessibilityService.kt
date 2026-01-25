package com.coc.zkqcode.utils.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class MyAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MyAccessibilityService"
        private var instance: MyAccessibilityService? = null

        fun getInstance(): MyAccessibilityService? {
            return instance
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected")
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events if needed
        println("got accessibility!")
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Accessibility Service Destroyed")
        instance = null
    }
}
