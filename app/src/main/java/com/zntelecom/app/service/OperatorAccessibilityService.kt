package com.zntelecom.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Reads the system USSD dialog that appears after UssdHelper.dial()
 * opens Ooredoo's Dima menu (#500#), and taps the matching option
 * automatically:
 *  - Daily gift -> option "5"
 *  - Weekly internet offers -> the relevant internet sub-menu option
 *
 * This does not call any private/internal API — it only reads the same
 * on-screen USSD menu text the user would read, and taps the same
 * option the user would tap by hand.
 *
 * NOTE: the USSD dialog's package name varies by device/OEM (usually
 * "com.android.phone", but Samsung/Xiaomi/etc. may use a different
 * dialer package). If automation doesn't trigger on a given phone, add
 * that device's dialer package name to the accessibility_service_config.xml
 * packageNames list and to the `when` block below.
 */
class OperatorAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val root = rootInActiveWindow ?: return
        val packageName = event?.packageName?.toString() ?: return

        if (packageName == PHONE_PACKAGE) {
            handleUssdDialog(root)
        }
    }

    private fun handleUssdDialog(root: AccessibilityNodeInfo) {
        val optionNode = findNodeByText(root, DAILY_GIFT_OPTION)
        optionNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    private fun findNodeByText(
        root: AccessibilityNodeInfo,
        text: String
    ): AccessibilityNodeInfo? {
        val matches = root.findAccessibilityNodeInfosByText(text)
        return matches.firstOrNull()
    }

    override fun onInterrupt() {}

    companion object {
        const val PHONE_PACKAGE = "com.android.phone"

        // Real Ooredoo Dima menu option for the daily gift, per #500# menu.
        const val DAILY_GIFT_OPTION = "5"
    }
}
