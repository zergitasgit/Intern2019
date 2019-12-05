package com.example.swipenavigationbar

import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager

class Utility {
    companion object {
        fun isAccessibilityEnabled(context: Context, id: String): Boolean {
            val am = context
                .getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

            val runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK)
            for (service in runningServices) {
                if (id == service.id) {
                    return true
                }
            }

            return false
        }
    }
}