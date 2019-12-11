package com.swipe.navigationbar

import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.provider.Settings.Secure
import android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
import android.text.TextUtils
import android.provider.Settings.SettingNotFoundException



class Utility {
    companion object {
        fun isAccessibilityEnabled(mContext: Context, id: String): Boolean {
            var accessibilityEnabled = 0

            try {
                accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED
                )
//                Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
            } catch (e: Settings.SettingNotFoundException) {
//                Log.e(
//                    TAG,
//                    "Error finding setting, default accessibility to not found: " + e.getMessage()
//                )
            }

            val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

            if (accessibilityEnabled == 1) {
//                Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------")
                val settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                if (settingValue != null) {
                    mStringColonSplitter.setString(settingValue)
                    while (mStringColonSplitter.hasNext()) {
                        val accessibilityService = mStringColonSplitter.next()

//                        Log.v(
//                            TAG,
//                            "-------------- > accessibilityService :: $accessibilityService $service"
//                        )
                        if (accessibilityService.equals(id, ignoreCase = true)) {
//                            Log.v(
//                                TAG,
//                                "We've found the correct setting - accessibility is switched on!"
//                            )
                            return true
                        }
                    }
                }
            } else {
//                Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
            }

            return false
        }
    }
}